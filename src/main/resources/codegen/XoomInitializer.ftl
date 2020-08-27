package ${packageName};

import io.vlingo.actors.Stage;
import io.vlingo.actors.World;
import io.vlingo.http.resource.Configuration;
import io.vlingo.http.resource.Resources;
import io.vlingo.http.resource.Server;
import io.vlingo.xoom.XoomInitializationAware;
import io.vlingo.xoom.actors.Settings;

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

import java.lang.Exception;
import java.lang.Integer;
import java.lang.String;

public class XoomInitializer implements XoomInitializationAware {

  private static final Integer DEFAULT_PORT = 18080;

  private static XoomInitializer instance;

  public final Server server;

  public final World world;

  public XoomInitializer(String[] args) {
    Settings.loadProperties();
    <#if blockingMessaging>
    Settings.enableBlockingMailbox();
    <#else>
    Settings.disableBlockingMailbox();
    </#if>
    world = World.start("${appName}", Settings.properties());
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

    Resources resources = Resources.are(
    <#list restResources as restResource>
        <#if restResource.last>
        ${restResource.objectName}.routes()
        <#else>
        ${restResource.objectName}.routes(),
        </#if>
    </#list>
    );

    server = Server.startWith(stage, resources, serverConfiguration.port(), serverConfiguration.sizing(), serverConfiguration.timing());

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
