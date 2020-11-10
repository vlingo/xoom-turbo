package ${packageName};

import java.util.Arrays;
import java.util.List;

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

import io.vlingo.actors.Definition;
import io.vlingo.actors.Protocols;
import io.vlingo.actors.Stage;
<#if persistentTypes?has_content>
import io.vlingo.symbio.store.state.StateTypeStateStoreMap;
</#if>
import io.vlingo.lattice.model.stateful.StatefulTypeRegistry;
<#if requireAdapters>
import io.vlingo.lattice.model.stateful.StatefulTypeRegistry.Info;
import io.vlingo.symbio.StateAdapterProvider;
</#if>
import io.vlingo.symbio.EntryAdapterProvider;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.dispatch.DispatcherControl;
import io.vlingo.symbio.store.dispatch.Dispatchable;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.xoom.actors.Settings;
import io.vlingo.xoom.storage.Model;
import io.vlingo.xoom.storage.StoreActorBuilder;
import io.vlingo.xoom.annotation.persistence.Persistence.StorageType;


public class ${storeProviderName} {
  private static ${storeProviderName} instance;

  public final StateStore store;
  <#list queries as query>
  public final ${query.protocolName} ${query.attributeName};
  </#list>

  public static ${storeProviderName} instance() {
    return instance;
  }

  public static ${storeProviderName} using(final Stage stage, final StatefulTypeRegistry registry) {
    final Dispatcher noop = new Dispatcher() {
      public void controlWith(final DispatcherControl control) { }
      public void dispatch(Dispatchable d) { }
    };

    return using(stage, registry, noop);
  }

  @SuppressWarnings("rawtypes")
  public static ${storeProviderName} using(final Stage stage, final StatefulTypeRegistry registry, final Dispatcher dispatcher) {
    if (instance != null) {
      return instance;
    }

<#if requireAdapters>
    final StateAdapterProvider stateAdapterProvider = new StateAdapterProvider(stage.world());
<#list adapters as stateAdapter>
    stateAdapterProvider.registerAdapter(${stateAdapter.sourceClass}.class, new ${stateAdapter.adapterClass}());
</#list>

</#if>
    new EntryAdapterProvider(stage.world()); // future use

<#list persistentTypes as persistentType>
    StateTypeStateStoreMap.stateTypeToStoreName(${persistentType}.class, ${persistentType}.class.getSimpleName());
</#list>

    final StateStore store =
            StoreActorBuilder.from(stage, Model.${model}, dispatcher, StorageType.STATE_STORE, Settings.properties(), true);

<#if requireAdapters>
<#list adapters as stateAdapter>
    registry.register(new Info(store, ${stateAdapter.sourceClass}.class, ${stateAdapter.sourceClass}.class.getSimpleName()));
</#list>
</#if>

    instance = new ${storeProviderName}(stage, store);

    return instance;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private ${storeProviderName}(final Stage stage, final StateStore store) {
    this.store = store;
    <#list queries as query>
    this.${query.attributeName} = stage.actorFor(${query.protocolName}.class, ${query.actorName}.class, store);
    </#list>
  }
}
