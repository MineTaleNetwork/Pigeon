package cc.minetale.pigeon;

import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents another Pigeon application, which a payload has been sent from.
 * <br>Allows for easier payload origin's identification and sending back callbacks/feedback.
 */
@Getter @AllArgsConstructor
public class PostalUnit {
    private String id;
    public void send(BasePayload payload) {
        Pigeon.getPigeon().sendTo(payload, this);
    }
}
