package io.eigr.spawn.internal;

import com.google.protobuf.GeneratedMessage;
import io.eigr.spawn.api.actors.ActorContext;
import io.eigr.spawn.api.actors.Value;

@FunctionalInterface
public interface ActionNoArgumentFunction<T extends GeneratedMessage> extends ActionEmptyFunction {

    Value handle(ActorContext context);
}
