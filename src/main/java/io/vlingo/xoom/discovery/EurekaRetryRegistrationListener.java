package io.vlingo.xoom.discovery;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.discovery.DiscoveryClient;
import io.micronaut.discovery.event.ServiceStartedEvent;
import io.micronaut.health.HealthStatus;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import io.reactivex.Single;
import io.vlingo.common.Scheduled;
import io.vlingo.common.Scheduler;
import io.vlingo.xoom.VlingoServer;
import io.vlingo.xoom.server.VlingoServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This event handler is responsible for healing a load-balanced discovery client that uses Eureka for its service
 * registry.
 *
 * @author Kenny Bastani
 */
@Singleton
@Requires(property = "eureka.client.registration.enabled", value = "true")
public class EurekaRetryRegistrationListener implements ApplicationEventListener<ServerStartupEvent> {
    private static final Logger log = LoggerFactory.getLogger(EurekaRetryRegistrationListener.class);

    @Override
    public void onApplicationEvent(ServerStartupEvent event) {
        VlingoServer vlingoServer = (VlingoServer) event.getSource();
        Scheduler scheduler = new Scheduler();
        VlingoServiceInstance serviceInstance = (VlingoServiceInstance) vlingoServer.getServiceInstance();
        scheduler.schedule(getEurekaRetryScheduler(event, scheduler, serviceInstance),
                event.getSource().getApplicationContext().getBean(DiscoveryClient.class), 5000, 5000);
    }

    private Scheduled<DiscoveryClient> getEurekaRetryScheduler(ServerStartupEvent event, Scheduler scheduler,
                                                               VlingoServiceInstance serviceInstance) {
        return (scheduled, data) -> {
            try {
                List<String> instances = Single.fromPublisher(data.getServiceIds())
                        .timeout(2000, TimeUnit.MILLISECONDS)
                        .doOnError(throwable -> {
                            log.info("Could not reach the discovery service, attempting retry...");
                            serviceInstance.setHealthStatus(HealthStatus.DOWN);
                        })
                        .doOnSuccess(strings -> {
                            log.info("Successfully contacted the discovery service for registration.");
                            if (serviceInstance.getHealthStatus().equals(HealthStatus.DOWN) ||
                                    serviceInstance.getHealthStatus().equals(HealthStatus.UNKNOWN)) {
                                serviceInstance.setHealthStatus(HealthStatus.UP);
                                event.getSource().getApplicationContext()
                                        .publishEvent(new ServiceStartedEvent(serviceInstance));
                            }
                        }).blockingGet();
                // Close the scheduler if there is no error after the blocking get
                scheduler.close();
            } catch (Exception ex) {
                log.info(ex.getMessage());
            }
        };
    }
}
