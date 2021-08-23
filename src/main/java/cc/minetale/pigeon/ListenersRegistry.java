package cc.minetale.pigeon;

import cc.minetale.pigeon.listeners.Listener;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import cc.minetale.pigeon.annotations.PayloadHandler;
import cc.minetale.pigeon.annotations.PayloadListener;
import cc.minetale.pigeon.payloads.bases.FeedbackPayload;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class ListenersRegistry {

    @Getter private final Set<Listener> listeners = new HashSet<>();

    public void callListeners(BasePayload payload) {
        try {
            for(Listener listener : listeners) {
                Class<? extends Listener> listenerClass = listener.getClass();
                for(Method method : listenerClass.getDeclaredMethods()) {
                    if(!isHandler(method) || !isHandlerApplicable(method, payload)) { continue; }
                    method.invoke(listener, payload);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public boolean registerListener(Listener listener) { return isListener(listener) && listeners.add(listener); }

    public static boolean isListener(Object listener) {
        Class<?> clazz = listener.getClass();
        return clazz.isAnnotationPresent(PayloadListener.class) && Listener.class.isAssignableFrom(clazz);
    }

    public static boolean isHandler(Method method) {
        return method.isAnnotationPresent(PayloadHandler.class) &&
                method.getParameterTypes().length == 1 &&
                BasePayload.class.isAssignableFrom(method.getParameterTypes()[0]);
    }

    public static boolean isHandlerApplicable(Method method, BasePayload payload) {
        return method.getParameterTypes()[0].isAssignableFrom(payload.getClass()) &&
                !(payload instanceof FeedbackPayload &&
                        !method.getAnnotation(PayloadHandler.class).requiredState()
                        .doesStateMeetRequirement(((FeedbackPayload) payload).getPayloadState()));
    }

}
