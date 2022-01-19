package cc.minetale.pigeon.payloads.bases;

import cc.minetale.pigeon.Pigeon;
import cc.minetale.pigeon.PostalUnit;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;

@Getter
public abstract class BasePayload {

    @JsonProperty("payloadId") protected String payloadId;
    @JsonProperty("origin") @Setter protected PostalUnit origin;

    /**
     * Override to calculate and generate values from the received data available on the payload for ease of use.
     * <br>Example might be getting a player wrapper from a unique identifier.
     */
    public abstract void receive();

    public BasePayload fromJson(String json) throws JsonProcessingException {
        final var mapper = Pigeon.getPigeon().getMapper();
        JsonNode fromJson = mapper.readTree(json);
        return mapper.treeToValue(fromJson, getClass());
    }

    public String toJson() throws JsonProcessingException {
        final var mapper = Pigeon.getPigeon().getMapper();
        return mapper.writeValueAsString(this);
    }

    public BasePayload createOfType() {
        try {
            return this.getClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

}
