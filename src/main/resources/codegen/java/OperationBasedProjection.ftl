package ${packageName};

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

import io.vlingo.lattice.model.projection.Projectable;
import io.vlingo.lattice.model.projection.StateStoreProjectionActor;
import io.vlingo.symbio.store.state.StateStore;

/**
 * See
 * <a href="https://docs.vlingo.io/vlingo-lattice/projections#implementing-with-the-statestoreprojectionactor">
 *   Implementing With the StateStoreProjectionActor
 * </a>
 */
public class ${projectionName} extends StateStoreProjectionActor<${dataName}> {
  private String becauseOf;

  public ${projectionName}() {
    this(${storeProviderName}.instance().store);
  }

  public ${projectionName}(final StateStore stateStore) {
    super(stateStore);
  }

  @Override
  protected ${dataName} currentDataFor(Projectable projectable) {
    becauseOf = projectable.becauseOf()[0];
    final ${stateName} state = projectable.object();
    final ${dataName} current = ${dataName}.from(state);
    return current;
  }

  @Override
  protected ${dataName} merge(${dataName} previousData, int previousVersion, ${dataName} currentData, int currentVersion) {
    ${dataName} merged;

    if (previousData == null) {
      previousData = currentData;
    }

    switch (${projectionSourceTypesName}.valueOf(becauseOf)) {
      <#list sourceNames as source>
      case ${source}:
        return ${dataName}.empty();   // TODO: implement actual merge
      </#list>
      default:
        merged = currentData;
    }

    return merged;
  }
}
