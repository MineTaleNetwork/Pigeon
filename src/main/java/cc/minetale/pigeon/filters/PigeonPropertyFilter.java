package cc.minetale.pigeon.filters;

import cc.minetale.pigeon.annotations.Transmit;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

public class PigeonPropertyFilter extends SimpleBeanPropertyFilter {
    @Override protected boolean include(BeanPropertyWriter writer) {
        final var annotation = writer.getAnnotation(Transmit.class);
        if(annotation != null) {
            System.out.println("Bson");
            System.out.println(annotation);
        }
        return super.include(writer);
    }

    @Override protected boolean include(PropertyWriter writer) {
        final var annotation = writer.getAnnotation(Transmit.class);
        if(annotation != null) {
            System.out.println("NonBson");
            System.out.println(annotation);
        }
        return super.include(writer);
    }
}
