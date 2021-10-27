package cc.minetale.pigeon;

import cc.minetale.pigeon.payloads.bases.BasePayload;
import cc.minetale.pigeon.feedback.Feedback;
import cc.minetale.pigeon.feedback.FeedbackState;
import cc.minetale.pigeon.payloads.bases.FeedbackPayload;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

    private final ConvertersRegistry convertersRegistry = new ConvertersRegistry();
    private final ListenersRegistry listenersRegistry = new ListenersRegistry();
    private final PayloadsRegistry payloadsRegistry = new PayloadsRegistry();

    private final Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
    private final Gson gson = new GsonBuilder().create();

    @Nullable private ScheduledFuture<?> defaultUpdater;

    public void initialize(String host, int port, String networkId, String unitId) {
        pigeon = this;

        this.postOffice = new PostOffice(this, host, port, networkId, unitId);

        this.convertersRegistry.registerConvertersInPackage("cc.minetale.pigeon.converters");
        this.payloadsRegistry.registerPayloadsInPackage("cc.minetale.pigeon.payloads");
    }

    public void acceptDelivery() {
        this.postOffice.setup();
    }

    public void broadcast(BasePayload payload) {
        if(payload instanceof FeedbackPayload) {
            var feedbackPayload = (FeedbackPayload) payload;
            if(feedbackPayload.getPayloadState() == FeedbackState.REQUEST) { setupFeedback(feedbackPayload, true); }
        }

        payload.setOrigin(this.postOffice.getUnit());

        this.postOffice.send("pigeon-broadcast", getTransmitReadyData(payload));
    }

    public void sendTo(BasePayload payload, PostalUnit target) {
        if(payload instanceof FeedbackPayload) {
            var feedbackPayload = (FeedbackPayload) payload;
            if(feedbackPayload.getPayloadState() == FeedbackState.REQUEST) { setupFeedback(feedbackPayload, true); }
        }

        payload.setOrigin(this.postOffice.getUnit());

        this.postOffice.send(target.getId(), getTransmitReadyData(payload));
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

    private String getTransmitReadyData(BasePayload payload) {
        return payload.getPayloadId() + "&" + payload.toJson();
    }

}
