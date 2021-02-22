package ${packageName}

import java.util.Arrays
import java.util.List

import io.vlingo.actors.Definition
import io.vlingo.actors.Protocols
import io.vlingo.actors.Stage
import io.vlingo.lattice.model.projection.ProjectionDispatcher
import io.vlingo.lattice.model.projection.ProjectionDispatcher.ProjectToDescription
import io.vlingo.lattice.model.projection.TextProjectionDispatcherActor
import io.vlingo.symbio.store.dispatch.Dispatcher

<#if imports?has_content>
<#list imports as import>
import ${import.qualifiedClassName}
</#list>
</#if>

@SuppressWarnings("rawtypes")
public class ProjectionDispatcherProvider {

  public companion object{
    var instance: ProjectionDispatcherProvider

    public fun instance(): ProjectionDispatcherProvider {
      return instance
    }

    public fun using(stage: Stage): ProjectionDispatcherProvider {
      if (instance != null) return instance

      val descriptions: List<ProjectToDescription> =
          Arrays.asList(
            <#list projectToDescriptions as projectToDescription>
                ${projectToDescription.initializationCommand}
            </#list>
          )

      val dispatcherProtocols: Protocols =
          stage.actorFor(
          Array<Class<?>> { Dispatcher::class.java, ProjectionDispatcher::class.java },
          Definition.has(TextProjectionDispatcherActor::class.java, Definition.parameters(descriptions)))

      val dispatchers: Protocols.Two<Dispatcher, ProjectionDispatcher> = Protocols.two(dispatcherProtocols)

      instance = ProjectionDispatcherProvider(dispatchers._1, dispatchers._2)

      return instance
    }
  }

  public val projectionDispatcher: ProjectionDispatcher
  public val storeDispatcher: Dispatcher

  constructor(storeDispatcher: Dispatcher, projectionDispatcher: ProjectionDispatcher) {
    this.storeDispatcher = storeDispatcher
    this.projectionDispatcher = projectionDispatcher
  }
}
