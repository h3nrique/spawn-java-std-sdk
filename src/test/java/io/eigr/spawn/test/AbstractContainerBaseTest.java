package io.eigr.spawn.test;

import io.eigr.spawn.api.Spawn;
import io.eigr.spawn.api.TransportOpts;
import io.eigr.spawn.api.exceptions.SpawnException;
import io.eigr.spawn.api.extensions.DependencyInjector;
import io.eigr.spawn.api.extensions.SimpleDependencyInjector;
import io.eigr.spawn.test.actors.ActorWithConstructor;
import io.eigr.spawn.test.actors.JoeActor;
import io.eigr.spawn.test.actors.StatelessNamedActor;
import io.eigr.spawn.test.actors.UnNamedActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.utility.DockerImageName;

public abstract class AbstractContainerBaseTest {

    private static final Logger log = LoggerFactory.getLogger(AbstractContainerBaseTest.class);
    private static GenericContainer<?> SPAWN_CONTAINER;
    private static final String spawnProxyImage = "ghcr.io/eigr/spawn-proxy:2.0.0-RC9";
    private static final String userFunctionPort = "8091";
    protected static Spawn spawnSystem;
    protected static final String spawnSystemName = "spawn-system-test";

    static {
        Testcontainers.exposeHostPorts(8091);

        SPAWN_CONTAINER = new GenericContainer<>(DockerImageName.parse(spawnProxyImage))
                .waitingFor(new LogMessageWaitStrategy()
                        .withRegEx(".*Proxy Application started successfully.*"))
                .withEnv("SPAWN_PROXY_LOGGER_LEVEL", "DEBUG")
                .withEnv("SPAWN_STATESTORE_KEY", "3Jnb0hZiHIzHTOih7t2cTEPEpY98Tu1wvQkPfq/XwqE=")
                .withEnv("PROXY_ACTOR_SYSTEM_NAME", spawnSystemName)
                .withEnv("PROXY_DATABASE_TYPE", "native")
                .withEnv("PROXY_DATABASE_DATA_DIR", "mnesia_data")
                .withEnv("NODE_COOKIE", "cookie-9ce3712b0c3ee21b582c30f942c0d4da-HLuZyQzy+nt0p0r/PVVFTp2tqfLom5igrdmwkYSuO+Q=")
                .withEnv("POD_NAMESPACE", spawnSystemName)
                .withEnv("POD_IP", spawnSystemName)
                .withEnv("PROXY_HTTP_PORT", "9004")
                .withEnv("USER_FUNCTION_PORT", userFunctionPort)
                .withEnv("USER_FUNCTION_HOST", "host.testcontainers.internal")
                .withExtraHost("host.testcontainers.internal", "host-gateway")
                .withExposedPorts(9004)
                .withAccessToHost(true);
        SPAWN_CONTAINER.start();

        DependencyInjector injector = SimpleDependencyInjector.createInjector();
        injector.bind(String.class, "Hello with Constructor");

        try {
            spawnSystem = new Spawn.SpawnSystem()
                    .create(spawnSystemName, injector)
                    .withActor(JoeActor.class)
                    .withActor(UnNamedActor.class)
                    .withActor(ActorWithConstructor.class)
                    .withActor(StatelessNamedActor.class)
                    .withTerminationGracePeriodSeconds(5)
                    .withTransportOptions(TransportOpts.builder()
                            .host(SPAWN_CONTAINER.getHost())
                            .port(8091)
                            .proxyPort(SPAWN_CONTAINER.getMappedPort(9004))
                            .build())
                    .build();

            spawnSystem.start();
            log.info(String.format("%s started", spawnSystemName));
        } catch (SpawnException e) {
            throw new RuntimeException(e);
        }
    }
}

