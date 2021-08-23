package cc.minetale.pigeon.annotations;

import cc.minetale.pigeon.feedback.RequiredState;
import cc.minetale.pigeon.payloads.bases.FeedbackPayload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(METHOD)
@Retention(RUNTIME)
public @interface PayloadHandler {
    /**
     * Used in conjunction with {@link FeedbackPayload} to determine what state the payload should be in to handle it.
     */
    RequiredState requiredState() default RequiredState.BOTH;
}
