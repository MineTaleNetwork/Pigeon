package cc.minetale.pigeon.payloads;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import lombok.Getter;

@Getter @Payload
public class AnnouncementPayload extends MessagePayload {

    @Transmit String title;
    @Transmit String type;

    public AnnouncementPayload() { payloadId = "pigeon-announcement"; }

    public AnnouncementPayload(String title, String type) {
        this();
        this.title = title;
        this.type = type;
    }

    @Override
    public void receive() {
        //Unused
    }

}
