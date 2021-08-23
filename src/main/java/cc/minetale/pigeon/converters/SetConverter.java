package cc.minetale.pigeon.converters;

import cc.minetale.pigeon.Converter;
import cc.minetale.pigeon.Pigeon;
import cc.minetale.pigeon.Utilities;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
public class SetConverter extends Converter<Set> {

    public SetConverter() { super(Set.class, true, true); }

    @Override
    public Set convertToValue(Type fieldType, JsonElement element) {
        return Utils.convertToValue(element, ((ParameterizedType) fieldType).getActualTypeArguments()[0]);
    }

    @Override @SuppressWarnings("unchecked")
    public JsonElement convertToSimple(Type fieldType, Set value) {
        return Utils.convertToSimple(value, ((ParameterizedType) fieldType).getActualTypeArguments()[0]);
    }

    public static final class Utils {
        private Utils() { }

        public static <V> Set<V> convertToValue(JsonElement element, Type setType) {
            if(element.isJsonNull() || !element.isJsonArray()) { return new HashSet<>(); }

            var array = element.getAsJsonArray();
            if(array.size() == 0) { return new HashSet<>(); }

            return Utilities.wildcardSetToTyped(array, setType);
        }

        public static <T> JsonElement convertToSimple(Set<T> value, Type setType) {
            if(value == null || value.isEmpty()) { return new JsonArray(); }

            JsonArray convertedList = new JsonArray();

            Converter<T> converter = Pigeon.getPigeon()
                    .getConvertersRegistry().getConverterForType(setType);

            for(T element : value) {
                convertedList.add(converter.convertToSimple(setType, element));
            }

            return convertedList;
        }
    }

}
