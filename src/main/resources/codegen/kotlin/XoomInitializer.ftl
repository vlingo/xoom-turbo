package ${packageName}

import io.vlingo.actors.Stage
import io.vlingo.actors.World
import io.vlingo.http.resource.*
import io.vlingo.xoom.XoomInitializationAware
import java.util.Arrays
<#if blockingMessaging>
import io.vlingo.xoom.scooter.plugin.mailbox.blocking.BlockingMailboxPlugin
</#if>

<#list imports as import>
import ${import.qualifiedClassName}
</#list>

import java.lang.Exception
import java.lang.Integer
import java.lang.String
import java.util.Arrays
import java.util.Collection
import java.util.stream.Collectors
import java.util.stream.Stream

public class XoomInitializer : XoomInitializationAware {

  companion object {
    val DEFAULT_PORT = 18080

    val instance: XoomInitializer

    public fun instance(): XoomInitializer {
      return instance
    }
  }

  public val server: Server

  public val world: World

  public constructor(args: Array<String>) {
    world = World.start("${appName}")
    <#if blockingMessaging>
    BlockingMailboxPlugin().start(world)
    </#if>
    val stage: Stage = world.stageNamed(${stageInstantiationVariables})

    val initializer: XoomInitializationAware =
          <#if customInitialization>
          ${xoomInitializerClass}()
          <#else>
          this
          </#if>

    initializer.onInit(stage)
    val serverConfiguration: Configuration = initializer.configureServer(stage, args)

    <#list registries as registry>
    val ${registry.objectName}: ${registry.className} = ${registry.className}(world)
    </#list>
    <#list providers as provider>
    ${provider.className}.using(${provider.arguments})
    </#list>

    <#list restResources as restResource>
    val ${restResource.objectName}: ${restResource.className} = ${restResource.className}(stage)
    </#list>

    val sseResources: Collection<Resource<?>> = Loader.resourcesFrom(initializer.sseConfiguration()).values()
    val feedResources: Collection<Resource<?>> = Loader.resourcesFrom(initializer.feedConfiguration()).values()
    val staticResources: Collection<Resource<?>> = Loader.resourcesFrom(initializer.staticFilesConfiguration()).values()
    val restResources: Collection<Resource<?>> = Arrays.asList(
          <#list restResources as restResource>
              <#if restResource.last>
              ${restResource.objectName}.routes()
              <#else>
              ${restResource.objectName}.routes(),
              </#if>
          </#list>
    )

    val resources: Array<Resource> =
            Stream.of(sseResources, feedResources, staticResources, restResources)
                    .flatMap{it.stream()}
                    .toArray{size -> arrayOfNulls<Resource<*>>(size)}

    server = Server.startWith(stage, Resources.are(resources), serverConfiguration.port(), serverConfiguration.sizing(), serverConfiguration.timing())

    Runtime.getRuntime().addShutdownHook(Thread{
      if (instance != null) {
        instance.server.stop()
        println("=========================")
        println("Stopping ${appName}.")
        println("=========================")
      }
    })
  }

  @JvmStatic
  public fun main(args: Array<String>) {
    println("=========================")
    println("service: ${appName}.")
    println("=========================")
    instance = XoomInitializer(args)
  }

  public void onInit(stage: Stage) {
  }

}
