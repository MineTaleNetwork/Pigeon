package cc.minetale.pigeon.payloads.bases;

import cc.minetale.pigeon.Pigeon;
import cc.minetale.pigeon.PostalUnit;
import cc.minetale.pigeon.converters.StringConverter;
import cc.minetale.pigeon.converters.UUIDConverter;
import cc.minetale.pigeon.Converter;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.feedback.FeedbackState;
import cc.minetale.pigeon.feedback.RequiredState;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.sun.jdi.InvalidTypeException;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Map;
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

    @Override
    public FeedbackPayload fromJson(String json) {
        JsonObject fromJson = Pigeon.getPigeon().getGson()
                .fromJson(json, JsonObject.class);

        this.origin = new PostalUnit(StringConverter.Utils.convertToValue(fromJson.get("origin")));

        this.payloadState = FeedbackState.values()[fromJson.get("payloadState").getAsInt()];
        this.feedbackID = UUIDConverter.Utils.convertToValue(fromJson.get("feedbackID"));

        Field[] fields = this.getClass().getDeclaredFields();
        for(Field field : fields) {
            if(!field.isAnnotationPresent(Transmit.class)) { continue; }

            for(Map.Entry<String, JsonElement> ent : fromJson.entrySet()) {
                String fieldName = ent.getKey();
                JsonElement value = ent.getValue();

                if(!fieldName.equals(field.getName())) { continue; }

                if(value.isJsonNull()) {
                    try {
                        if (field.trySetAccessible())
                            field.set(this, null);

                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                    break;
                }

                Converter<?> converter = Pigeon.getPigeon()
                        .getConvertersRegistry()
                        .getConverterForType(field.getGenericType());

                if(converter == null) {
                    try {
                        throw new InvalidTypeException("Field \"" + fieldName + "\" is of type \"" + field.getType().getName() + "\" which doesn't have a converter available.");
                    } catch (InvalidTypeException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                try {
                    if(field.trySetAccessible())
                        field.set(this, converter.convertToValue(field.getGenericType(), value));

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return this;
    }

    @Override
    public String toJson() {
        JsonObject data = new JsonObject();

        data.add("origin", StringConverter.Utils.convertToSimple(this.origin.getId()));

        data.add("payloadState", new JsonPrimitive(this.payloadState.ordinal()));
        data.add("feedbackID", UUIDConverter.Utils.convertToSimple(this.feedbackID));

        Field[] fields = this.getClass().getDeclaredFields();
        for(Field field : fields) {
            Transmit annotation = field.getAnnotation(Transmit.class);
            if(annotation == null) { continue; }

            var requiredState = annotation.direction();
            if((requiredState == RequiredState.REQUEST && payloadState != FeedbackState.REQUEST) ||
                    (requiredState == RequiredState.RESPONSE && payloadState != FeedbackState.RESPONSE)) { continue; }

            String fieldName = field.getName();

            Object value = null;
            try {
                if(field.trySetAccessible())
                    value = field.get(this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if(value == null) {
                data.add(fieldName, JsonNull.INSTANCE);
                continue;
            }

            Converter<Object> converter = Pigeon.getPigeon()
                    .getConvertersRegistry()
                    .getConverterForType(field.getGenericType());

            if(converter == null) {
                try {
                    throw new InvalidTypeException("Field \"" + fieldName + "\" is of type \"" + field.getType().getName() + "\" which doesn't have a converter available.");
                } catch (InvalidTypeException e) {
                    e.printStackTrace();
                }

                continue;
            }

            data.add(fieldName, converter.convertToSimple(field.getGenericType(), value));
        }

        return Pigeon.getPigeon().getGson().toJson(data);
    }
}
