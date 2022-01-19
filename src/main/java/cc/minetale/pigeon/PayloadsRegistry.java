package cc.minetale.pigeon;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public class PayloadsRegistry {

    @Getter private final Set<BasePayload> payloadTypes = new HashSet<>();

    public void registerPayloadsInPackage(String prefix) {
        try (var scanResult =
                     new ClassGraph()
                             .enableClassInfo()
                             .enableAnnotationInfo()
                             .acceptPackages(prefix)
                             .scan()) {
            for (ClassInfo routeClassInfo : scanResult.getClassesWithAnnotation(Payload.class.getName())) {
                Class<? extends BasePayload> clazz = (Class<? extends BasePayload>) routeClassInfo.loadClass();
                if(isPayload(clazz)) { registerPayload(clazz); }
            }
        }
    }

    public boolean registerPayload(Class<?> payloadClass) {
        try {
            BasePayload payload = (BasePayload) payloadClass.getDeclaredConstructor().newInstance();
            payloadTypes.add(payload);
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public BasePayload getPayloadById(String id) {
        BasePayload payload = null;
        for (BasePayload type : payloadTypes) {
            if (!type.getPayloadId().equals(id)) { continue; }
            payload = type.createOfType();
        }
        return payload;
    }

    public static boolean isPayload(Class<?> clazz) {
        return clazz.isAnnotationPresent(Payload.class) && BasePayload.class.isAssignableFrom(clazz);
    }

}
