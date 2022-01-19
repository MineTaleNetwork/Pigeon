package cc.minetale.pigeon.payloads.bases;

import cc.minetale.pigeon.PostalUnit;
import cc.minetale.pigeon.feedback.FeedbackState;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

@Getter
public abstract class FeedbackPayload extends BasePayload {

    protected UUID feedbackID;

    protected long payloadTimeout;
    protected FeedbackState payloadState;

    @Nullable protected Consumer<? extends FeedbackPayload> feedback;

    /**
     * Sends a response to the {@link PostalUnit} where this request payload originated from.
     * @param response payload to be sent as a response to this one
     * @return true - if payload has been sent successfully ;
     * false - if the payload provided isn't in a response state or it's not of the same class as this payload
     */
    public boolean sendResponse(FeedbackPayload response) {
        if(!this.getClass().equals(response.getClass()) || response.payloadState != FeedbackState.RESPONSE) { return false; }
        response.feedbackID = this.feedbackID;
        origin.send(response);
        return true;
    }

}
