namespace ${packageName};

using io.vlingo.actors.Stage;
using io.vlingo.common.Completes;
using io.vlingo.xoom.annotation.autodispatch.Handler.Three;
using io.vlingo.xoom.annotation.autodispatch.Handler.Two;
using io.vlingo.xoom.annotation.autodispatch.HandlerEntry;

<#list usings as using>
using ${using.qualifiedClassName};
</#list>
<#if useCQRS>
using System.Collections.Generic;
</#if>

public class ${autoDispatchHandlersMappingName} {

  <#list handlerIndexes as index>
  ${index}
  </#list>

  <#list handlerEntries as entry>
  ${entry}
  </#list>
  public static HandlerEntry<Two<${dataName}, ${stateName}>> ADAPT_STATE_HANDLER =
          HandlerEntry.of(ADAPT_STATE, ${dataName}::from);

  <#if useCQRS>
  public static HandlerEntry<Two<Completes<Collection<${dataName}>>, ${queriesName}>> QUERY_ALL_HANDLER =
          HandlerEntry.of(${queryAllIndexName}, ${queriesName}::${queryAllMethodName});
  </#if>

}