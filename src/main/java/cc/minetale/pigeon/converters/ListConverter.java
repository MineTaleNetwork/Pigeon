package cc.minetale.pigeon.converters;

import cc.minetale.pigeon.Converter;
import cc.minetale.pigeon.Pigeon;
import cc.minetale.pigeon.Utilities;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListConverter extends Converter<List> {

    public ListConverter() { super(List.class, true, true); }

    @Override
    public List convertToValue(Type fieldType, JsonElement element) {
        return Utils.convertToValue(element, ((ParameterizedType) fieldType).getActualTypeArguments()[0]);
    }

    @Override @SuppressWarnings("unchecked")
    public JsonElement convertToSimple(Type fieldType, List value) {
        return Utils.convertToSimple(value, ((ParameterizedType) fieldType).getActualTypeArguments()[0]);
    }

    public static final class Utils {
        private Utils() { }

        public static <V> List<V> convertToValue(JsonElement element, Type listType) {
            if(element.isJsonNull() || !element.isJsonArray()) { return new ArrayList<>(); }

            var array = element.getAsJsonArray();
            if(array.size() == 0) { return new ArrayList<>(); }

            return Utilities.wildcardListToTyped(array, listType);
        }

        public static <T> JsonElement convertToSimple(List<T> value, Type listType) {
            if(value == null || value.isEmpty()) { return new JsonArray(); }

            JsonArray convertedList = new JsonArray();

            Converter<T> converter = Pigeon.getPigeon()
                    .getConvertersRegistry().getConverterForType(listType);

            for(T element : value) {
                convertedList.add(converter.convertToSimple(listType, element));
            }

            return convertedList;
        }
    }

}
