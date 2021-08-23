package cc.minetale.pigeon.converters;

import cc.minetale.pigeon.Converter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

public class LongConverter extends Converter<Long> {

    public LongConverter() { super(Long.class, true, false); }

    @Override
    public Long convertToValue(Type fieldType, JsonElement element) {
        return element.getAsLong();
    }

    @Override
    public JsonElement convertToSimple(Type fieldType, Long value) {
        return new JsonPrimitive(value);
    }

}
