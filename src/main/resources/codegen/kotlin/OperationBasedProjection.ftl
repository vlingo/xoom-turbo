package ${packageName}

<#list imports as import>
import ${import.qualifiedClassName}
</#list>

import io.vlingo.lattice.model.projection.Projectable
import io.vlingo.lattice.model.projection.StateStoreProjectionActor

/**
 * See
 * <a href="https://docs.vlingo.io/vlingo-lattice/projections#implementing-with-the-statestoreprojectionactor">
 *   Implementing With the StateStoreProjectionActor
 * </a>
 */
public class ${projectionName} : StateStoreProjectionActor<${dataName}> {
  var becauseOf: String

  public constructor() : super(${storeProviderName}.instance().store){

  }

  protected override fun currentDataFor(projectable: Projectable): ${dataName} {
    becauseOf = projectable.becauseOf()[0]
    val state: ${stateName} = projectable.object()
    val current: ${dataName} = ${dataName}.from(state)
    return current
  }

  protected override fun merge(previousData: ${dataName}, previousVersion: Int, currentData: ${dataName}, currentVersion: Int): ${dataName} {
    var merged: ${dataName}

    if (previousData == null) {
      previousData = currentData
    }

    switch (${projectionSourceTypesName}.valueOf(becauseOf)) {
      <#list sourceNames as source>
      case ${source}:
        return ${dataName}.empty()   // TODO: implement actual merge
      </#list>
      default:
        merged = currentData
    }

    return merged
  }
}
