package cc.minetale.pigeon.converters;

import cc.minetale.pigeon.Converter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

public class BooleanConverter extends Converter<Boolean> {

    //Why tf can you get a class of boolean but not other primitives
    public BooleanConverter() { super(boolean.class, true, false); }

    @Override
    public Boolean convertToValue(Type fieldType, JsonElement element) {
        return element.getAsBoolean();
    }

    @Override
    public JsonElement convertToSimple(Type fieldType, Boolean value) {
        return new JsonPrimitive(value);
    }

}
