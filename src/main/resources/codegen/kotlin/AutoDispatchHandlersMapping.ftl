package ${packageName}

import io.vlingo.actors.Stage
import io.vlingo.common.Completes
import io.vlingo.xoom.annotation.autodispatch.Handler.Three
import io.vlingo.xoom.annotation.autodispatch.Handler.Two
import io.vlingo.xoom.annotation.autodispatch.HandlerEntry

<#list imports as import>
import ${import.qualifiedClassName}
</#list>
<#if useCQRS>
import java.util.Collection
</#if>

public class ${autoDispatchHandlersMappingName} {

  <#list handlerIndexes as index>
  ${index}
  </#list>

  <#list handlerEntries as entry>
  ${entry}
  </#list>
  public companion object {
    public val ADAPT_STATE_HANDLER: HandlerEntry<Two<${dataName}, ${stateName}>> =
        HandlerEntry.of(ADAPT_STATE, ${dataName}::from)
  }

  <#if useCQRS>
  public companion object {
    public val QUERY_ALL_HANDLER: HandlerEntry<Two<Completes<Collection<${dataName}>>, ${queriesName}>> =
        HandlerEntry.of(${queryAllIndexName}, ${queriesName}::${queryAllMethodName})
  }
  </#if>

}
