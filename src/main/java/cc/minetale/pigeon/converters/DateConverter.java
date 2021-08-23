package cc.minetale.pigeon.converters;

import cc.minetale.pigeon.Converter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;

public class DateConverter extends Converter<Date> {

    public DateConverter() { super(Date.class, true, false); }

    @Override
    public Date convertToValue(Type fieldType, JsonElement element) {
        return Utils.convertToValue(element);
    }

    @Override
    public JsonElement convertToSimple(Type fieldType, Date value) {
        return Utils.convertToSimple(value);
    }

    public static class Utils {
        public static Date convertToValue(JsonElement element) {
            var calendar = Calendar.getInstance();
            calendar.setTimeInMillis(element.getAsLong());
            return calendar.getTime();
        }

        public static JsonElement convertToSimple(Date value) {
            return new JsonPrimitive(value.getTime());
        }
    }

}
