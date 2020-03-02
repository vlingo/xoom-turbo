package io.vlingo.xoom.management;

import io.micronaut.context.ApplicationContext;
import io.micronaut.management.endpoint.beans.BeansEndpoint;
import io.micronaut.management.endpoint.env.EnvironmentEndpoint;
import io.micronaut.management.endpoint.health.DetailsVisibility;
import io.micronaut.management.endpoint.health.HealthEndpoint;
import io.micronaut.management.endpoint.loggers.LoggersEndpoint;
import io.micronaut.management.endpoint.routes.RoutesEndpoint;
import io.vlingo.common.Completes;
import io.vlingo.http.resource.RequestHandler;
import io.vlingo.http.resource.RequestHandler1;
import io.vlingo.xoom.management.endpoints.StepFlowEndpoint;
import io.vlingo.xoom.management.endpoints.OpenApiEndpoint;
import io.vlingo.xoom.resource.annotations.Resource;
import io.vlingo.xoom.resource.Endpoint;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.http.Response.Status.Ok;
import static io.vlingo.http.resource.ResourceBuilder.get;

@Resource
public class ManagementResource implements Endpoint {

    private final String ENDPOINT_NAME = "Management";
    private final String ENDPOINT_VERSION = "1.0.0";
    private final ApplicationContext applicationContext;

    public ManagementResource(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Completes<BeansEndpoint> getBeansEndpoint() {
        return Completes.withSuccess(applicationContext.getBean(BeansEndpoint.class));
    }

    public Completes<HealthEndpoint> getHealthEndpoint() {
        return Completes.withSuccess(applicationContext.getBean(HealthEndpoint.class));
    }

    public Completes<RoutesEndpoint> getRoutesEndpoint() {
        return Completes.withSuccess(applicationContext.getBean(RoutesEndpoint.class));
    }

    public Completes<EnvironmentEndpoint> getEnvironmentEndpoint() {
        return Completes.withSuccess(applicationContext.getBean(EnvironmentEndpoint.class));
    }

    public Completes<LoggersEndpoint> getLoggersEndpoint() {
        return Completes.withSuccess(applicationContext.getBean(LoggersEndpoint.class));
    }

    public Completes<StepFlowEndpoint> getFlowEndpoint() {
        return Completes.withSuccess(applicationContext.getBean(StepFlowEndpoint.class));
    }

    public Completes<OpenApiEndpoint> getOpenApiEndpoint() {
        return Completes.withSuccess(applicationContext.getBean(OpenApiEndpoint.class));
    }

    @Override
    public String getName() {
        return ENDPOINT_NAME;
    }

    @Override
    public String getVersion() {
        return ENDPOINT_VERSION;
    }

    @Override
    public RequestHandler[] getHandlers() {
        List<RequestHandler> handlers = Stream.of(
                get("/health").handle(() -> response(Ok, getHealthEndpoint()
                        .andThen(healthEndpoint -> {
                            healthEndpoint.setDetailsVisible(DetailsVisibility.ANONYMOUS);
                            return healthEndpoint.getHealth(null).blockingGet();
                        })))
                        .onError(this::getErrorResponse),
                get("/beans").handle(() -> response(Ok, getBeansEndpoint()
                        .andThen(beansEndpoint -> beansEndpoint.getBeans().blockingGet())))
                        .onError(this::getErrorResponse),
                get("/routes").handle(() -> response(Ok, getRoutesEndpoint()
                        .andThen(routesEndpoint -> routesEndpoint.getRoutes().blockingGet())))
                        .onError(this::getErrorResponse),
                get("/env").handle(() -> response(Ok, getEnvironmentEndpoint()
                        .andThen(EnvironmentEndpoint::getEnvironmentInfo)))
                        .onError(this::getErrorResponse),
                get("/loggers").handle(() -> response(Ok, getLoggersEndpoint()
                        .andThen(loggersEndpoint -> loggersEndpoint.loggers().blockingGet())))
                        .onError(this::getErrorResponse),
                get("/openapi").handle(() -> response(Ok, getOpenApiEndpoint()
                        .andThen(OpenApiEndpoint::getSpecification)))
                        .onError(this::getErrorResponse)).collect(Collectors.toList());

        if (getFlowEndpoint().await() != null) {
            handlers.add(get("/flows/{name}")
                    .param(String.class)
                    .handle(processorEndpointHandler())
                    .onError(this::getErrorResponse));
        }

        return handlers.toArray(new RequestHandler[]{});
    }

    public RequestHandler1.Handler1<String> processorEndpointHandler() {
        return (name) -> response(Ok, getFlowEndpoint().andThen((endpoint) -> endpoint.getMap(name)));
    }
}
