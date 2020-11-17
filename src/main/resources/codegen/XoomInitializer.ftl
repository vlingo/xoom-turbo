package ${packageName};

import io.vlingo.actors.Stage;
import io.vlingo.actors.World;
import io.vlingo.http.resource.*;
import io.vlingo.xoom.XoomInitializationAware;
import java.util.Arrays;
<#if blockingMessaging>
import io.vlingo.xoom.scooter.plugin.mailbox.blocking.BlockingMailboxPlugin;
</#if>

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

import java.lang.Exception;
import java.lang.Integer;
import java.lang.String;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class XoomInitializer implements XoomInitializationAware {

  private static final Integer DEFAULT_PORT = 18080;

  private static XoomInitializer instance;

  public final Server server;

  public final World world;

  public XoomInitializer(String[] args) {
    world = World.start("${appName}");
    <#if blockingMessaging>
    new BlockingMailboxPlugin().start(world);
    </#if>
    final Stage stage = world.stageNamed(${stageInstantiationVariables});

    final XoomInitializationAware initializer =
          <#if customInitialization>
          new ${xoomInitializerClass}();
          <#else>
          this;
          </#if>

    initializer.onInit(stage);
    final Configuration serverConfiguration = initializer.configureServer(stage, args);

    <#list registries as registry>
    final ${registry.className} ${registry.objectName} = new ${registry.className}(world);
    </#list>
    <#list providers as provider>
    ${provider.className}.using(${provider.arguments});
    </#list>

    <#list restResources as restResource>
    final ${restResource.className} ${restResource.objectName} = new ${restResource.className}(stage);
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

    final Resource[] resources =
            Stream.of(sseResources, feedResources, staticResources, restResources)
                    .flatMap(Collection::stream).collect(Collectors.toList())
                    .toArray(new Resource<?>[]{});

    server = Server.startWith(stage, Resources.are(resources), serverConfiguration.port(), serverConfiguration.sizing(), serverConfiguration.timing());

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
         if (instance != null) {
        instance.server.stop();
        System.out.println("=========================");
        System.out.println("Stopping ${appName}.");
        System.out.println("=========================");
        }
    }));
  }

  public static void main(String[] args) throws Exception {
    System.out.println("=========================");
        System.out.println("service: ${appName}.");
        System.out.println("=========================");
        instance = new XoomInitializer(args);
  }

  public void onInit(Stage stage) {
  }

  public static XoomInitializer instance() {
    return instance;
  }

}
