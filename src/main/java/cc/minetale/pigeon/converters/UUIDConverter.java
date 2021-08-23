package cc.minetale.pigeon.converters;

import cc.minetale.pigeon.Converter;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.util.UUID;

public class UUIDConverter extends Converter<UUID> {

    public UUIDConverter() { super(UUID.class, true, false); }

    @Override
    public UUID convertToValue(Type fieldType, JsonElement element) {
        return Utils.convertToValue(element);
    }

    @Override
    public JsonElement convertToSimple(Type fieldType, UUID value) {
        return Utils.convertToSimple(value);
    }

    public static final class Utils {
        private Utils() { }

        public static UUID convertToValue(JsonElement element) {
            return UUID.fromString(StringConverter.Utils.convertToValue(element.getAsJsonArray()));
        }

        public static JsonElement convertToSimple(UUID value) {
            return StringConverter.Utils.convertToSimple(value.toString());
        }
    }

}
