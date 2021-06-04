package ${packageName};

import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.cluster.model.Properties;
import io.vlingo.xoom.http.resource.*;
import io.vlingo.xoom.lattice.grid.Grid;
import io.vlingo.xoom.turbo.Boot;
import io.vlingo.xoom.turbo.ComponentRegistry;
import io.vlingo.xoom.turbo.XoomInitializationAware;
<#if blockingMessaging>
import io.vlingo.xoom.turbo.scooter.plugin.mailbox.blocking.BlockingMailboxPlugin;
</#if>

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

import java.lang.Exception;
import java.lang.Integer;
import java.lang.String;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class XoomInitializer implements XoomInitializationAware {

  private final Grid grid;
  private final Server server;
  private static XoomInitializer instance;

  private XoomInitializer(final String[] args) throws Exception {
    final XoomInitializationAware initializer =
          <#if customInitialization>
          new ${xoomInitializerClass}();
          <#else>
          this;
          </#if>

    grid = Boot.start("${appName}", initializer.parseNodeName(args), initializer.clusterProperties());
    <#if blockingMessaging>
    new BlockingMailboxPlugin().start(grid.world());
    </#if>

    initializer.onInit(grid);

    final Configuration serverConfiguration = initializer.configureServer(grid, args);

    <#if exchangeBootstrapName?has_content>
    final io.vlingo.xoom.turbo.exchange.ExchangeInitializer exchangeInitializer =
      ComponentRegistry.has(io.vlingo.xoom.turbo.exchange.ExchangeInitializer.class) ?
      ComponentRegistry.withType(io.vlingo.xoom.turbo.exchange.ExchangeInitializer.class) : ${exchangeBootstrapName}();

    exchangeInitializer.init(grid);
    </#if>
    <#list registries as registry>
    final ${registry.className} ${registry.objectName} = new ${registry.className}(grid.world());
    </#list>
    <#list providers as provider>
    ${provider.className}.using(${provider.arguments});
    </#list>

    <#list restResources as restResource>
    final ${restResource.className} ${restResource.objectName} = new ${restResource.className}(grid);
    </#list>

    final Collection<Resource<?>> sseResources = Loader.resourcesFrom(initializer.sseConfiguration()).values();
    final Collection<Resource<?>> feedResources = Loader.resourcesFrom(initializer.feedConfiguration()).values();
    final Collection<Resource<?>> staticResources = Loader.resourcesFrom(initializer.staticFilesConfiguration()).values();
    final Collection<Resource<?>> restResources = Arrays.asList(
          <#list restResources as restResource>
              <#if restResource.last>
              ${restResource.objectName}.routes()
              <#else>
              ${restResource.objectName}.routes(),
              </#if>
          </#list>
    );
    final SinglePageApplicationConfiguration spaConf = initializer.singlePageApplicationResource();
    final Collection<? extends Resource<?>> spaResources = spaConf != null
          ? Collections.singleton(new SinglePageApplicationResource(spaConf.rootPath(), spaConf.contextPath()).routes())
          : Collections.emptySet();

    final Resource[] resources =
            Stream.of(sseResources, feedResources, staticResources, restResources, spaResources)
                    .flatMap(Collection::stream).collect(Collectors.toList())
                    .toArray(new Resource<?>[]{});


    server = Server.startWith(grid.world().stage(), Resources.are(resources), serverConfiguration.filters(), serverConfiguration.port(), serverConfiguration.sizing(), serverConfiguration.timing());

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
         if (instance != null) {
        instance.server.stop();
        System.out.println("=========================");
        System.out.println("Stopping ${appName}.");
        System.out.println("=========================");
        }
    }));
  }

  public void onInit(final Grid grid) {
  }

  public static void main(final String[] args) throws Exception {
    System.out.println("=========================");
    System.out.println("service: ${appName}.");
    System.out.println("=========================");
    instance = new XoomInitializer(args);
  }

  public static XoomInitializer instance() {
    return instance;
  }

  public Server server() {
    return server;
  }

  public void stopServer() throws Exception {
    if (instance == null) {
      throw new IllegalStateException("${appName} server not running");
    }
    instance.server.stop();
    instance.grid.world().terminate();
  }
}
