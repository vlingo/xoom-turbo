package io.vlingo.xoom;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.Environment;
import io.micronaut.core.io.socket.SocketUtils;
import io.micronaut.discovery.ServiceInstance;
import io.micronaut.discovery.event.ServiceShutdownEvent;
import io.micronaut.discovery.event.ServiceStartedEvent;
import io.micronaut.health.HealthStatus;
import io.micronaut.runtime.ApplicationConfiguration;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import io.vlingo.http.resource.Configuration;
import io.vlingo.http.resource.Resource;
import io.vlingo.http.resource.Resources;
import io.vlingo.http.resource.Server;
import io.vlingo.xoom.config.ServerConfiguration;
import io.vlingo.xoom.events.SceneStartedEvent;
import io.vlingo.xoom.resource.handlers.CachedStaticFilesResource;
import io.vlingo.xoom.resource.Endpoint;
import io.vlingo.xoom.server.VlingoScene;
import io.vlingo.xoom.server.VlingoServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The {@link VlingoServer} is a Micronaut bootstrapper for loading and auto-configuring a vlingo/http server.
 * Vlingo/http is a reactive client-server framework built on the actor model. Micronaut is a JVM microframework
 * for building light-weight compile-time native JVM applications. This class implements an
 * {@link EmbeddedServer} and provides lifecycle application context management and configuration classes at
 * startup.
 *
 * @author Kenny Bastani
 * @author Graeme Rocher
 */
@Singleton
public class VlingoServer implements EmbeddedServer {
    private static final Logger log = LoggerFactory.getLogger(VlingoServer.class);
    private final String host;
    private Server server;
    private final VlingoScene vlingoScene;
    private final Set<Resource> resources;
    private final Set<Endpoint> endpoints;
    private final ApplicationContext applicationContext;
    private final ApplicationConfiguration applicationConfiguration;
    private boolean isRunning = false;
    private ServiceInstance serviceInstance;

    /**
     * Bootstrap the application context and configuration for starting the vlingo/http server.
     *
     * @param applicationContext       is the Micronaut application context.
     * @param applicationConfiguration is the application configuration for vlingo/http.
     * @param vlingoScene              is the vlingo/actors scene for the vlingo/http server.
     * @param endpoints                is a stream of HTTP request/response endpoints for the vlingo/http server.
     */
    public VlingoServer(ApplicationContext applicationContext, ApplicationConfiguration applicationConfiguration,
                        VlingoScene vlingoScene, Stream<Endpoint> endpoints) {
        // Load the world context with auto-configured settings
        this.endpoints = endpoints.collect(Collectors.toSet());
        this.resources = this.endpoints.stream().map(Endpoint::getResource).collect(Collectors.toSet());
        this.resources.add(new CachedStaticFilesResource().routes());
        this.applicationContext = applicationContext;
        this.applicationConfiguration = applicationConfiguration;
        this.vlingoScene = vlingoScene;
        this.host = Optional.ofNullable(this.vlingoScene.getServerConfiguration().getHost())
                .orElseGet(() -> Optional.ofNullable(System.getenv(Environment.HOSTNAME))
                        .orElse(SocketUtils.LOCALHOST));
    }

    public Server getServer() {
        return server;
    }

    public VlingoScene getVlingoScene() {
        return vlingoScene;
    }

    public Set<Resource> getResources() {
        return resources;
    }

    public Set<Endpoint> getEndpoints() {
        return endpoints;
    }

    @Override
    public int getPort() {
        return Math.toIntExact(vlingoScene.getServerConfiguration().getPort());
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public String getScheme() {
        return vlingoScene.getServerConfiguration().getScheme();
    }

    @Override
    public URL getURL() {
        try {
            return getURI().toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL argument: " + getURI());
        }
    }

    @Override
    public URI getURI() {
        return URI.create(getScheme() + "://" + getHost() + ":" + getPort());
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public ApplicationConfiguration getApplicationConfiguration() {
        return applicationConfiguration;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public @Nonnull
    VlingoServer start() {
        if (!isRunning) {
            if (!vlingoScene.isRunning()) {
                vlingoScene.start();
            }

            // Start the server with auto-configured settings
            this.server = Server.startWith(vlingoScene.getWorld().stage(),
                    Resources.are(resources.toArray(new Resource[0])),
                    vlingoScene.getServerConfiguration().getPort(),
                    Configuration.Sizing.defineWith(10, 16, 100,
                            65535 * 2),
                    Configuration.Timing.define());

            serviceInstance = applicationContext.createBean(VlingoServiceInstance.class, this);
            applicationContext.publishEvent(new ServerStartupEvent(this));
            if(serviceInstance.getHealthStatus() == HealthStatus.DOWN)
                applicationContext.publishEvent(new ServiceStartedEvent(serviceInstance));
            applicationContext.publishEvent(new SceneStartedEvent(vlingoScene));
            isRunning = true;
        } else {
            throw new RuntimeException("A Vlingo Xoom server is already running in the current Micronaut context");
        }

        log.info(ServerConfiguration.getBanner());
        return this;
    }

    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    @Override
    public synchronized @Nonnull
    VlingoServer stop() {
        //applicationContext.publishEvent(new ServerShutdownEvent(this));
        if (serviceInstance != null) {
            applicationContext.publishEvent(new ServiceShutdownEvent(serviceInstance));
        }
        if (applicationContext.isRunning()) {
            applicationContext.stop();
        }
        return this;
    }

    @PreDestroy
    @Override
    public void close() {
        if (isRunning) {
            vlingoScene.close();
            server.stop();
            isRunning = false;
            log.info("Stopped embedded Vlingo Xoom server at " + getURI().toASCIIString());
        } else {
            throw new RuntimeException("A Vlingo Xoom server is not running in the current Micronaut context");
        }
    }

    @Override
    public boolean isKeepAlive() {
        return false;
    }
}
