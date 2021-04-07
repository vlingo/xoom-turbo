package ${packageName};

import java.util.Arrays;
import java.util.List;

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

import io.vlingo.actors.Definition;
import io.vlingo.actors.Stage;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry.Info;
import io.vlingo.symbio.EntryAdapterProvider;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.dispatch.NoOpDispatcher;
import io.vlingo.symbio.store.dispatch.DispatcherControl;
import io.vlingo.symbio.store.dispatch.Dispatchable;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.xoom.actors.Settings;
import io.vlingo.xoom.storage.Model;
import io.vlingo.xoom.storage.StoreActorBuilder;
import io.vlingo.xoom.annotation.persistence.Persistence.StorageType;

@SuppressWarnings("ALL")
public class ${storeProviderName}  {
  private static ${storeProviderName} instance;

  public final Journal<String> journal;

  public static ${storeProviderName}  instance() {
    return instance;
  }

  public static ${storeProviderName} using(final Stage stage, final SourcedTypeRegistry registry) {
    return using(stage, registry, new NoOpDispatcher());
 }

  public static ${storeProviderName} using(final Stage stage, final SourcedTypeRegistry registry, final Dispatcher ...dispatchers) {
    if (instance != null) {
      return instance;
    }

    final EntryAdapterProvider entryAdapterProvider = EntryAdapterProvider.instance(stage.world());

<#list adapters as entryAdapter>
    entryAdapterProvider.registerAdapter(${entryAdapter.sourceClass}.class, new ${entryAdapter.adapterClass}());
</#list>

    final Journal<String> journal =
              StoreActorBuilder.from(stage, Model.${model}, Arrays.asList(dispatchers), StorageType.JOURNAL, Settings.properties(), true);

<#list aggregates as aggregate>
    registry.register(new Info(journal, ${aggregate}.class, ${aggregate}.class.getSimpleName()));
</#list>

    instance = new ${storeProviderName}(journal);

    return instance;
  }

  private ${storeProviderName}(final Journal<String> journal) {
    this.journal = journal;
  }

}
