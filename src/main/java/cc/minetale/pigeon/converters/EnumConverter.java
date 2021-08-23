package cc.minetale.pigeon.converters;

import cc.minetale.pigeon.Converter;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;

public class EnumConverter extends Converter<Enum> {

    public EnumConverter() { super(Enum.class, true, true); }

    @Override @SuppressWarnings("unchecked")
    public Enum convertToValue(Type fieldType, JsonElement element) {
        return Utils.convertToValue(fieldType, element);
    }

    @Override
    public JsonElement convertToSimple(Type fieldType, Enum value) {
        return Utils.convertToSimple(value);
    }

    public static final class Utils {
        private Utils() { }

        @SuppressWarnings("unchecked")
        public static <T extends Enum<T>> T convertToValue(Type enumType, JsonElement element) {
            return Enum.valueOf((Class<T>) enumType, StringConverter.Utils.convertToValue(element));
        }

        public static JsonElement convertToSimple(Enum<?> value) {
            return StringConverter.Utils.convertToSimple(value.toString());
        }
    }

}
