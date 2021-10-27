package cc.minetale.pigeon.converters;

import cc.minetale.pigeon.Converter;
import cc.minetale.pigeon.Pigeon;
import cc.minetale.pigeon.Utilities;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class StringConverter extends Converter<String> {

    public StringConverter() { super(String.class, true, false); }

    @Override
    public String convertToValue(Type fieldType, JsonElement element) {
        return Utils.convertToValue(element);
    }

    @Override
    public JsonElement convertToSimple(Type fieldType, String value) {
        return Utils.convertToSimple(value);
    }

    public static final class Utils {
        private Utils() { }

        public static String convertToValue(JsonElement element) {
            return Utilities.decodeString(element);
        }

        public static JsonElement convertToSimple(String value) {
            return Pigeon.getPigeon().getGson().toJsonTree(value.getBytes(StandardCharsets.UTF_16));
        }
    }

}
