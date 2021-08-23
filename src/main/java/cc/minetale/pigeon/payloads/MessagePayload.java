package cc.minetale.pigeon.payloads;

import cc.minetale.pigeon.payloads.bases.BasePayload;
import cc.minetale.pigeon.annotations.Transmit;
import lombok.Getter;

@Getter
public abstract class MessagePayload extends BasePayload {

    @Transmit String content;

    protected MessagePayload() { payloadId = "pigeon-message"; }

    protected MessagePayload(String content) {
        this();
        this.content = content;
    }

}
