package ${packageName};

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

import io.vlingo.lattice.model.projection.Projectable;
import io.vlingo.lattice.model.projection.StateStoreProjectionActor;
import io.vlingo.symbio.Source;

/**
 * See
 * <a href="https://docs.vlingo.io/vlingo-lattice/projections#implementing-with-the-statestoreprojectionactor">
 *   StateStoreProjectionActor
 * </a>
 */
public class ${projectionName} extends StateStoreProjectionActor<${dataName}> {

  public ${projectionName}() {
    super(${storeProviderName}.instance().store);
  }

  @Override
  protected ${dataName} currentDataFor(final Projectable projectable) {
    <#if stateful>
    final ${stateName} state = projectable.object();
    final ${dataName} current = ${dataName}.from(state);
    return current;
    <#else>
    return ${dataName}.empty();
    </#if>
  }

  @Override
  protected ${dataName} merge(${dataName} previousData, int previousVersion, ${dataName} currentData, int currentVersion) {

    if (previousData == null) {
      previousData = currentData;
    }

    for (final Source<?> event : sources()) {
      switch (${projectionSourceTypesName}.valueOf(event.typeName())) {
      <#list sourceNames as source>
        case ${source}:
          return ${dataName}.empty();   // TODO: implement actual merge
      </#list>
        default:
          logger().warn("Event of type " + event.typeName() + " was not matched.");
          break;
      }
    }

    return previousData;
  }
}
