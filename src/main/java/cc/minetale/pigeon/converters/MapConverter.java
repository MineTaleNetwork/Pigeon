package cc.minetale.pigeon.converters;

import cc.minetale.pigeon.Converter;
import cc.minetale.pigeon.Pigeon;
import cc.minetale.pigeon.Utilities;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MapConverter extends Converter<Map> {

    public MapConverter() { super(Map.class, true, true); }

    @Override
    public Map convertToValue(Type fieldType, JsonElement element) {
        var paramTypes = ((ParameterizedType) fieldType).getActualTypeArguments();

        var keyType = (Class<?>) paramTypes[0];
        var valueType = (Class<?>) paramTypes[1];

        return Utils.convertToValue(element, keyType, valueType);
    }

    @Override
    public JsonElement convertToSimple(Type fieldType, Map value) {
        var paramTypes = ((ParameterizedType) fieldType).getActualTypeArguments();

        var keyType = (Class<?>) paramTypes[0];
        var valueType = (Class<?>) paramTypes[1];

        return Utils.convertToSimple(value, keyType, valueType);
    }

    public static final class Utils {
        private Utils() { }

        public static <K, V> Map<K, V> convertToValue(JsonElement element, Class<K> keyType, Class<V> valueType) {
            if(element.isJsonNull() || !element.isJsonObject()) { return new HashMap<>(); }

            JsonObject map = element.getAsJsonObject();
            if(map.size() == 0) { return new HashMap<>(); }

            return Utilities.wildcardMapToTyped(map, keyType, valueType);
        }

        public static <K, V> JsonElement convertToSimple(Map<K, V> value, Class<K> keyType, Class<V> valueType) {
            if(value == null || value.isEmpty()) { return new JsonObject(); }

            var convertedMap = new JsonObject();

            Converter<K> keyConverter = Pigeon.getPigeon()
                    .getConvertersRegistry().getConverterForType(keyType);

            Converter<V> valueConverter = Pigeon.getPigeon()
                    .getConvertersRegistry().getConverterForType(valueType);

            var keys = new JsonArray();
            var values = new JsonArray();

            for(Map.Entry<K, V> ent : value.entrySet()) {
                keys.add(keyConverter.convertToSimple(keyType, ent.getKey()));
                values.add(valueConverter.convertToSimple(valueType, ent.getValue()));
            }

            convertedMap.add("keys", keys);
            convertedMap.add("values", values);

            return convertedMap;
        }
    }

}
