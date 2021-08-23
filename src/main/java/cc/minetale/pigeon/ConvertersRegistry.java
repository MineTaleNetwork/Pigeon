package cc.minetale.pigeon;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import lombok.Getter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class ConvertersRegistry {

    @Getter private final Set<Converter<?>> converters = new HashSet<>();

    public void registerConvertersInPackage(String prefix) {
        try (var scanResult =
                     new ClassGraph()
                             .enableClassInfo()
                             .whitelistPackages(prefix)
                             .scan()) {
            for (ClassInfo routeClassInfo : scanResult.getSubclasses(Converter.class.getName())) {
                Class<? extends Converter> clazz = (Class<? extends Converter>) routeClassInfo.loadClass();

                if(!isConverter(clazz)) { continue; }
                registerConverter(clazz);
            }
        }
    }

    public boolean registerConverter(Class<?> converterClass) {
        try {
            Converter<?> converter = (Converter<?>) converterClass.getDeclaredConstructor().newInstance();
            converters.add(converter);
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public <T> Converter<T> getConverterForType(Type type) {
        Type trueType = type;
        if(type instanceof ParameterizedType) {
            trueType = ((ParameterizedType) type).getRawType();
        }

        //Preferred converter is always going to use the standard check
        Converter<T> preferredConverter = null;

        for(Converter<?> converter : converters) {
            if(converter.standardCheck) {
                if(preferredConverter != null && converter.type.isAssignableFrom(preferredConverter.type)) { continue; }
                if((converter.allowForSubtypes && !converter.type.isAssignableFrom((Class<?>) trueType)) ||
                        (!converter.allowForSubtypes && !converter.type.equals(trueType))) { continue; }
                preferredConverter = (Converter<T>) converter;
            } else {
                if(converter.customCheck(trueType)) { return (Converter<T>) converter; }
            }
        }

        return preferredConverter;
    }

    public static boolean isConverter(Class<?> clazz) {
        return Converter.class.isAssignableFrom(clazz);
    }

}
