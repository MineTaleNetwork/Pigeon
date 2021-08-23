package cc.minetale.pigeon.payloads.bases;

import cc.minetale.pigeon.Converter;
import cc.minetale.pigeon.Pigeon;
import cc.minetale.pigeon.PostalUnit;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.converters.StringConverter;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.sun.jdi.InvalidTypeException;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@Getter
public abstract class BasePayload {

    protected String payloadId;
    @Setter protected PostalUnit origin;

    /**
     * Override to calculate and generate values from the received data available on the payload for ease of use.
     * <br>Example might be getting a player wrapper from a unique identifier.
     */
    public abstract void receive();

    public BasePayload fromJson(String json) {
        JsonObject fromJson = Pigeon.getGson()
                .fromJson(json, JsonObject.class);

        this.origin = new PostalUnit(StringConverter.Utils.convertToValue(fromJson.get("origin")));

        Field[] fields = this.getClass().getDeclaredFields();
        for(Field field : fields) {
            if(!field.isAnnotationPresent(Transmit.class)) { continue; }

            for(Map.Entry<String, JsonElement> ent : fromJson.entrySet()) {
                String fieldName = ent.getKey();
                JsonElement value = ent.getValue();

                if (!fieldName.equals(field.getName())) { continue; }

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

                if (converter == null) {
                    try {
                        throw new InvalidTypeException("Field \"" + fieldName + "\" is of type \"" + field.getType().getName() + "\" which doesn't have a converter available.");
                    } catch (InvalidTypeException e) {
                        e.printStackTrace();
                    }

                    continue;
                }

                try {
                    if (field.trySetAccessible())
                        field.set(this, converter.convertToValue(field.getGenericType(), value));

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return this;
    }

    public String toJson() {
        JsonObject data = new JsonObject();

        data.add("origin", StringConverter.Utils.convertToSimple(origin.getId()));

        Field[] fields = this.getClass().getDeclaredFields();
        for(Field field : fields) {
            if(!field.isAnnotationPresent(Transmit.class)) { continue; }

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

            if (converter == null) {
                try {
                    throw new InvalidTypeException("Field \"" + fieldName + "\" is of type \"" + field.getType().getName() + "\" which doesn't have a converter available.");
                } catch (InvalidTypeException e) {
                    e.printStackTrace();
                }

                continue;
            }

            data.add(fieldName, converter.convertToSimple(field.getGenericType(), value));
        }

        return Pigeon.getGson().toJson(data);
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
