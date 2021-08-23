package cc.minetale.pigeon.converters;

import cc.minetale.pigeon.Converter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

public class ShortConverter extends Converter<Short> {

    public ShortConverter() { super(Short.class, true, false); }

    @Override
    public Short convertToValue(Type fieldType, JsonElement element) {
        return element.getAsShort();
    }

    @Override
    public JsonElement convertToSimple(Type fieldType, Short value) {
        return new JsonPrimitive(value);
    }

}
