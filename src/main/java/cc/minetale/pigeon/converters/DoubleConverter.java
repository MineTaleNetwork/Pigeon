package cc.minetale.pigeon.converters;

import cc.minetale.pigeon.Converter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

public class DoubleConverter extends Converter<Double> {

    public DoubleConverter() { super(Double.class, true, false); }

    @Override
    public Double convertToValue(Type fieldType, JsonElement element) {
        return element.getAsDouble();
    }

    @Override
    public JsonElement convertToSimple(Type fieldType, Double value) {
        return new JsonPrimitive(value);
    }

}
