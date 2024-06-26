package io.eigr.spawn.api.actors.annotations.stateful;

import com.google.protobuf.GeneratedMessage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface StatefulNamedActor {
    String value() default "";

    String name() default "";

    Class<? extends GeneratedMessage> stateType();

    long deactivatedTimeout() default 60000;

    long snapshotTimeout() default 50000;

    String channel() default "";
}
