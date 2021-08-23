package cc.minetale.pigeon.feedback;

import cc.minetale.pigeon.payloads.bases.FeedbackPayload;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Getter
public class Feedback {
    @Getter private static final Map<UUID, Feedback> feedbacks = new ConcurrentHashMap<>();

    private UUID id;
    private long expireAt;
    private boolean removeOnReceive;

    private Consumer<? extends FeedbackPayload> consumer;

    public Feedback(UUID id, long expireAt, boolean removeOnReceive, Consumer<? extends FeedbackPayload> consumer) {
        this.id = id;
        this.expireAt = expireAt;
        this.removeOnReceive = removeOnReceive;

        this.consumer = consumer;
    }

    public void remove() {
        feedbacks.remove(this.getId());
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= expireAt;
    }
}
