package cc.minetale.pigeon;

import cc.minetale.pigeon.payloads.bases.BasePayload;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents another Pigeon application, which a payload has been sent from.
 * <br>Allows for easier payload origin's identification and sending back callbacks/feedback.
 */
public record PostalUnit(@JsonProperty("id") String id) {
    public void send(BasePayload payload) {
        Pigeon.getPigeon().sendTo(payload, this);
    }
}
