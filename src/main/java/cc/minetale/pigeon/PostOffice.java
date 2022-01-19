package cc.minetale.pigeon;

import cc.minetale.pigeon.payloads.bases.BasePayload;
import cc.minetale.pigeon.feedback.Feedback;
import cc.minetale.pigeon.feedback.FeedbackState;
import cc.minetale.pigeon.payloads.bases.FeedbackPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Delivery;
import lombok.Getter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * Manages sending and receiving messages through a RabbitMQ broker.
 */
@Getter
public class PostOffice {

    public static final String EXCHANGE = "pigeon";

    private final Pigeon pigeon;

    private final String host;
    private final int port;

    private final String username;
    private final String password;

    private final String virtualHost;

    private String networkId;
    private PostalUnit unit;

    private Connection rabbitMqConnection;

    @Getter private Channel channel;

    private String queue;

    public PostOffice(Pigeon pigeon, String host, int port, String networkId, String unitId) {
        this.pigeon = pigeon;

        this.host = host;
        this.port = port;

        this.username = System.getProperty("pigeonUsername", "guest");
        this.password = System.getProperty("pigeonPassword", "guest");

        this.virtualHost = System.getProperty("pigeonVirtualHost", "/");

        this.networkId = networkId;
        this.unit = new PostalUnit(unitId);
    }

    public void setup() {
        try {
            var factory = new ConnectionFactory();
            factory.setHost(host);
            factory.setPort(port);

            factory.setUsername(username);
            factory.setPassword(password);

            factory.setVirtualHost(virtualHost);

            this.rabbitMqConnection = factory.newConnection();

            this.channel = rabbitMqConnection.createChannel();
            this.channel.exchangeDeclare(EXCHANGE, "direct");

            this.queue = this.channel.queueDeclare().getQueue();

            bindQueues();

            this.channel.basicConsume(this.queue, true, this::receive, consumerTag -> {});
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void receive(String consumerTag, Delivery delivery) {
        var retrievedMessage = new String(delivery.getBody(), StandardCharsets.UTF_8);

        String[] data = getDataFromBody(retrievedMessage);

        String payloadId = data[0];
        String payloadData = data[1];

        var payloadsRegistry = this.pigeon.getPayloadsRegistry();

        BasePayload basePayload = payloadsRegistry.getPayloadById(payloadId);
        if (basePayload == null) { return; }

        BasePayload payload;
        try {
            payload = basePayload.fromJson(payloadData);
        } catch(JsonProcessingException e) {
            e.printStackTrace();
            return;
        }

        if(this.unit.id().equals(payload.getOrigin().id())) { return; }

        if(payload instanceof FeedbackPayload feedbackPayload) {
            if(feedbackPayload.getPayloadState() == FeedbackState.RESPONSE) {
                var feedback = Feedback.getFeedbacks().get(feedbackPayload.getFeedbackID());
                if(feedback != null) {
                    var consumer = (Consumer<FeedbackPayload>) feedback.getConsumer();
                    if (!feedback.isExpired()) {
                        if(consumer != null) { consumer.accept(feedbackPayload); }
                        if (feedback.isRemoveOnReceive()) {
                            feedback.remove();
                        }
                    } else {
                        if(consumer != null) { consumer.accept(null); }
                        feedback.remove();
                    }
                }
            }
        }

        this.pigeon.getListenersRegistry().callListeners(payload);
    }

    public void send(String routingKey, String message) {
        try {
            if (this.channel != null && this.channel.isOpen()) {
                this.channel.basicPublish(EXCHANGE, this.networkId + ":" + routingKey, null, message.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (this.channel != null && this.channel.isOpen()) {
                this.channel.close();
            }
            if(this.rabbitMqConnection != null && this.rabbitMqConnection.isOpen()) {
                this.rabbitMqConnection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bindQueues() {
        try {
            this.channel.queueBind(this.queue, EXCHANGE, this.networkId + ":" + "pigeon-broadcast");
            this.channel.queueBind(this.queue, EXCHANGE, this.networkId + ":" + this.unit.id());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public String[] getDataFromBody(String body) {
        var split = body.split("&");

        var payloadId = split[0];
        var payloadData = String.join("&", Arrays.copyOfRange(split, 1, split.length));

        return new String[]{ payloadId, payloadData };
    }

}
