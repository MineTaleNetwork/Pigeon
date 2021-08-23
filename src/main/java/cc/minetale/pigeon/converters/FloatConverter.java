package cc.minetale.pigeon.converters;

import cc.minetale.pigeon.Converter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

public class FloatConverter extends Converter<Float> {

    public FloatConverter() { super(Float.class, true, false); }

    @Override
    public Float convertToValue(Type fieldType, JsonElement element) {
        return element.getAsFloat();
    }

    @Override
    public JsonElement convertToSimple(Type fieldType, Float value) {
        return new JsonPrimitive(value);
    }

}
