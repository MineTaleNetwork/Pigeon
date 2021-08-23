package cc.minetale.pigeon.converters;

import cc.minetale.pigeon.Converter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.awt.*;
import java.lang.reflect.Type;

public class ColorConverter extends Converter<Color> {

    public ColorConverter() { super(Color.class, true, false); }

    @Override
    public Color convertToValue(Type fieldType, JsonElement element) {
        JsonObject data = element.getAsJsonObject();
        return new Color(
                data.get("r").getAsInt(),
                data.get("g").getAsInt(),
                data.get("b").getAsInt(),
                data.get("a").getAsInt());
    }

    @Override
    public JsonElement convertToSimple(Type fieldType, Color value) {
        JsonObject data = new JsonObject();

        data.add("r", new JsonPrimitive(value.getRed()));
        data.add("g", new JsonPrimitive(value.getGreen()));
        data.add("b", new JsonPrimitive(value.getBlue()));
        data.add("a", new JsonPrimitive(value.getAlpha()));

        return data;
    }

}
