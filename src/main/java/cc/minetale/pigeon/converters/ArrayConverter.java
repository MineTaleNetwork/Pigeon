package cc.minetale.pigeon.converters;

import cc.minetale.pigeon.Converter;
import cc.minetale.pigeon.Pigeon;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

public class ArrayConverter extends Converter<Object[]> {

    public ArrayConverter() { super(null, false, false); }

    @Override
    public boolean customCheck(Type type) {
        return ((Class<?>) type).isArray();
    }

    @Override
    public Object[] convertToValue(Type fieldType, JsonElement element) {
        return Utils.convertToValue(element, ((Class<?>) fieldType).getComponentType());
    }

    @Override
    public JsonElement convertToSimple(Type fieldType, Object[] value) {
        return Utils.convertToSimple(value, ((Class<?>) fieldType).getComponentType());
    }

    public static final class Utils {
        private Utils() { }

        public static Object[] convertToValue(JsonElement element, Class<?> arrayType) {
            if(element.isJsonNull() || !element.isJsonArray()) { return new Object[0]; }

            var jsonArray = element.getAsJsonArray();

            if(jsonArray.size() == 0) { return new Object[0]; }

            Object[] convertedArray = (Object[]) Array.newInstance(arrayType, jsonArray.size());

            Converter<?> converter = Pigeon.getPigeon()
                    .getConvertersRegistry().getConverterForType(arrayType);

            for(int i = 0; i < jsonArray.size(); i++) {
                convertedArray[i] = converter.convertToValue(arrayType, jsonArray.get(i));
            }

            return convertedArray;
        }

        public static JsonElement convertToSimple(Object[] value, Class<?> arrayType) {
            if(value.length == 0) { return new JsonArray(); }

            Converter<Object> converter = Pigeon.getPigeon()
                    .getConvertersRegistry().getConverterForType(arrayType);

            var convertedArray = new JsonArray();

            for(Object element : value) {
                convertedArray.add(converter.convertToSimple(arrayType, element));
            }

            return convertedArray;
        }
    }

}
