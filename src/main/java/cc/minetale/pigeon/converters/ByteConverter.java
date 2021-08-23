package cc.minetale.pigeon.converters;

import cc.minetale.pigeon.Converter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

public class ByteConverter extends Converter<Byte> {

    public ByteConverter() { super(Byte.class, true, false); }

    @Override
    public Byte convertToValue(Type fieldType, JsonElement element) {
        return element.getAsByte();
    }

    @Override
    public JsonElement convertToSimple(Type fieldType, Byte value) {
        return new JsonPrimitive(value);
    }

}
