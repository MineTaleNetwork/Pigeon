package cc.minetale.pigeon.converters;

import cc.minetale.pigeon.Converter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

public class CharacterConverter extends Converter<Character> {

    public CharacterConverter() { super(Character.class, true, false); }

    @Override
    public Character convertToValue(Type fieldType, JsonElement element) {
        return element.getAsString().charAt(0);
    }

    @Override
    public JsonElement convertToSimple(Type fieldType, Character value) {
        return new JsonPrimitive(value);
    }

}
