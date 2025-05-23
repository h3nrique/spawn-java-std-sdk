package io.eigr.spawn.internal.transport.client;

import io.spawn.Protocol;
import io.eigr.spawn.api.exceptions.ActorCreationException;
import io.eigr.spawn.api.exceptions.ActorInvocationException;
import io.eigr.spawn.api.exceptions.ActorRegistrationException;

public interface SpawnClient {

    Protocol.RegistrationResponse register(Protocol.RegistrationRequest registration) throws ActorRegistrationException;
    Protocol.SpawnResponse spawn(Protocol.SpawnRequest registration) throws ActorCreationException;
    Protocol.InvocationResponse invoke(Protocol.InvocationRequest request) throws ActorInvocationException;

    void invokeAsync(Protocol.InvocationRequest request);
}

