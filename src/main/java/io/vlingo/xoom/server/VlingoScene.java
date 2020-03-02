package io.vlingo.xoom.server;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.LifeCycle;
import io.micronaut.context.annotation.Context;
import io.micronaut.runtime.ApplicationConfiguration;
import io.vlingo.actors.World;
import io.vlingo.xoom.config.ServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * The {@link VlingoScene} is a lifecycle management class for the vlingo/actors context used in vlingo/http.
 *
 * @author Kenny Bastani
 */
@Context
public class VlingoScene implements LifeCycle<VlingoScene> {
    private static Logger log = LoggerFactory.getLogger(VlingoScene.class);
    private World world;
    private final ApplicationConfiguration applicationConfiguration;
    private final ServerConfiguration serverConfiguration;
    private final ApplicationContext applicationContext;
    private boolean isRunning;

    public VlingoScene(ServerConfiguration serverConfiguration,
                       ApplicationConfiguration applicationConfiguration,
                       ApplicationContext applicationContext) {
        this.applicationConfiguration = applicationConfiguration;
        this.serverConfiguration = serverConfiguration;
        this.applicationContext = applicationContext;
    }

    public World getWorld() {
        return world;
    }

    public ApplicationConfiguration getApplicationConfiguration() {
        return applicationConfiguration;
    }

    public ServerConfiguration getServerConfiguration() {
        return serverConfiguration;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Nonnull
    @Override
    public VlingoScene start() {
        if (!isRunning) {
            this.world = World.startWithDefaults(applicationConfiguration.getName().orElse("application"));
            log.info("New scene created: " + this.world.stage().name());
            this.isRunning = true;
        } else {
            throw new RuntimeException("A Vlingo Xoom scene is already running in the current Micronaut context");
        }
        return this;
    }

    @Nonnull
    @Override
    public VlingoScene stop() {
        if (isRunning) {
            world.stage().stop();
            world.terminate();
            isRunning = false;
        }
        return this;
    }

    @Override
    public void close() {
        this.stop();
    }

    @Nonnull
    @Override
    public VlingoScene refresh() {
        this.stop();
        return this.start();
    }
}
