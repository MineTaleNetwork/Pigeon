package cc.minetale.pigeon.converters;

import cc.minetale.pigeon.Converter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

public class IntegerConverter extends Converter<Integer> {

    public IntegerConverter() { super(Integer.class, true, false); }

    @Override
    public Integer convertToValue(Type fieldType, JsonElement element) {
        return element.getAsInt();
    }

    @Override
    public JsonElement convertToSimple(Type fieldType, Integer value) {
        return new JsonPrimitive(value);
    }

}
