package cc.minetale.pigeon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class Utilities {

    private Utilities() { }

    public static String decodeString(JsonElement msgBytes) {
        var array = msgBytes.getAsJsonArray();

        var bytes = new byte[array.size()];
        for (int i = 0; i < array.size(); i++) {
            bytes[i] = array.get(i).getAsByte();
        }

        return new String(bytes, StandardCharsets.UTF_16);
    }

    public static <T> List<T> wildcardListToTyped(@NotNull JsonArray array, Type type) {
        if(array.size() == 0) { return new ArrayList<>(); }

        List<T> convertedList = new ArrayList<>();
        Converter<T> converter = Pigeon.getPigeon()
                .getConvertersRegistry().getConverterForType(type);

        for(JsonElement element : array) {
            convertedList.add(converter.convertToValue(type, element));
        }

        return convertedList;
    }

    public static <K, V> Map<K, V> wildcardMapToTyped(JsonObject map, Type keyType, Type valueType) {
        if(map.entrySet().isEmpty()) { return new HashMap<>(); }

        Map<K, V> convertedMap = new HashMap<>();

        Converter<K> keyConverter = Pigeon.getPigeon()
                .getConvertersRegistry().getConverterForType(keyType);

        Converter<V> valueConverter = Pigeon.getPigeon()
                .getConvertersRegistry().getConverterForType(valueType);

        if(!map.has("keys") || !map.has("values")) { return new HashMap<>(); }

        var keys = map.get("keys").getAsJsonArray();
        var values = map.get("values").getAsJsonArray();

        if(values.size() < keys.size()) { return new HashMap<>(); }

        for(int i = 0; i < keys.size(); i++) {
            K key = keyConverter.convertToValue(keyType, keys.get(i));
            V value = valueConverter.convertToValue(valueType, values.get(i));
            convertedMap.put(key, value);
        }

        return convertedMap;
    }

    public static <T> Set<T> wildcardSetToTyped(@NotNull JsonArray array, Type type) {
        if(array.size() == 0) { return new HashSet<>(); }

        Set<T> convertedSet = new HashSet<>();
        Converter<T> converter = Pigeon.getPigeon()
                .getConvertersRegistry().getConverterForType(type);

        for(JsonElement element : array) {
            convertedSet.add(converter.convertToValue(type, element));
        }

        return convertedSet;
    }

}
