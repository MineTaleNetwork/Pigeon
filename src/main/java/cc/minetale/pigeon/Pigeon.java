package cc.minetale.pigeon;

import cc.minetale.pigeon.feedback.Feedback;
import cc.minetale.pigeon.feedback.FeedbackState;
import cc.minetale.pigeon.filters.PigeonPropertyFilter;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import cc.minetale.pigeon.payloads.bases.FeedbackPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Getter
public class Pigeon {

    @Getter private static Pigeon pigeon;

    private PostOffice postOffice;

    private final ListenersRegistry listenersRegistry = new ListenersRegistry();
    private final PayloadsRegistry payloadsRegistry = new PayloadsRegistry();

    private ObjectMapper mapper;

    @Nullable private ScheduledFuture<?> defaultUpdater;

    public void initialize(String host, int port, String networkId, String unitId, ObjectMapper mapper) {
        pigeon = this;
        this.postOffice = new PostOffice(this, host, port, networkId, unitId);
        this.payloadsRegistry.registerPayloadsInPackage("cc.minetale.pigeon.payloads");
        this.mapper = mapper
                .setFilterProvider(new SimpleFilterProvider()
                        .addFilter("feedbackPayloads", new PigeonPropertyFilter()));
    }

    public void acceptDelivery() {
        this.postOffice.setup();
    }

    public void broadcast(BasePayload payload) {
        if(payload instanceof FeedbackPayload feedbackPayload) {
            if(feedbackPayload.getPayloadState() == FeedbackState.REQUEST) { setupFeedback(feedbackPayload, true); }
        }

        payload.setOrigin(this.postOffice.getUnit());

        try {
            final var data = getTransmitReadyData(payload);
            this.postOffice.send("pigeon-broadcast", data);
        } catch(JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void sendTo(BasePayload payload, PostalUnit target) {
        if(payload instanceof FeedbackPayload feedbackPayload) {
            if(feedbackPayload.getPayloadState() == FeedbackState.REQUEST) { setupFeedback(feedbackPayload, true); }
        }

        payload.setOrigin(this.postOffice.getUnit());

        try {
            final var data = getTransmitReadyData(payload);
            this.postOffice.send(target.id(), data);
        } catch(JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        for(Feedback feedback : Feedback.getFeedbacks().values()) {
            if(feedback.isExpired()) {
                feedback.getConsumer().accept(null);
                feedback.remove();
            }
        }
    }

    public void setupDefaultUpdater() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        this.defaultUpdater = executor.scheduleAtFixedRate(this::update, 0, 1, TimeUnit.SECONDS);
    }

    private void setupFeedback(FeedbackPayload payload, boolean removeOnReceive) {
        UUID id = payload.getFeedbackID();

        var feedback = new Feedback(id,
                System.currentTimeMillis() + payload.getPayloadTimeout(),
                removeOnReceive,
                payload.getFeedback());

        Feedback.getFeedbacks().put(id, feedback);
    }

    private String getTransmitReadyData(BasePayload payload) throws JsonProcessingException {
        return payload.getPayloadId() + "&" + payload.toJson();
    }

}
