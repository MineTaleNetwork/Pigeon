package cc.minetale.pigeon.annotations;

import cc.minetale.pigeon.feedback.RequiredState;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
public @interface Transmit {
    RequiredState direction() default RequiredState.REQUEST;
}
