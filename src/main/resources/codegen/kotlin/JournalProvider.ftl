package ${packageName}

import java.util.Arrays
import java.util.List

<#list imports as import>
import ${import.qualifiedClassName}
</#list>

import io.vlingo.actors.Definition
import io.vlingo.actors.Stage
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry.Info
import io.vlingo.symbio.EntryAdapterProvider
import io.vlingo.symbio.store.dispatch.Dispatcher
import io.vlingo.symbio.store.dispatch.NoOpDispatcher
import io.vlingo.symbio.store.dispatch.DispatcherControl
import io.vlingo.symbio.store.dispatch.Dispatchable
import io.vlingo.symbio.store.journal.Journal
import io.vlingo.xoom.actors.Settings
import io.vlingo.xoom.storage.Model
import io.vlingo.xoom.storage.StoreActorBuilder
import io.vlingo.xoom.annotation.persistence.Persistence.StorageType

public class ${storeProviderName}  {

  public val journal: Journal<String>

  public companion object {
    var instance: ${storeProviderName}

    public fun instance(): ${storeProviderName} {
    return instance
    }

    public fun using(stage: Stage, registry: SourcedTypeRegistry): ${storeProviderName} {
    return using(stage, registry, NoOpDispatcher())
    }

    @SuppressWarnings({ "unchecked", "unused" })
    public fun using(stage: Stage, registry: SourcedTypeRegistry, vararg dispatchers: Dispatcher): ${storeProviderName} {
    if (instance != null) {
    return instance
    }

    val entryAdapterProvider = EntryAdapterProvider.instance(stage.world())

      <#list adapters as entryAdapter>
        entryAdapterProvider.registerAdapter(${entryAdapter.sourceClass}::class.java, ${entryAdapter.adapterClass}())
      </#list>

    val journal: Journal<String> =
      StoreActorBuilder.from(stage, Model.${model}, Arrays.asList(dispatchers), StorageType.JOURNAL, Settings.properties(), true)

          <#list aggregates as aggregate>
            registry.register(Info(journal, ${aggregate}::class.java, ${aggregate}::class.java.getSimpleName()))
          </#list>

      instance = ${storeProviderName}(journal)

      return instance
    }
  }

  constructor(journal: Journal<String>) {
    this.journal = journal
  }

}
