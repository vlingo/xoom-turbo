package ${packageName};

import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;
import io.vlingo.xoom.annotation.autodispatch.Handler.Three;
import io.vlingo.xoom.annotation.autodispatch.Handler.Two;
import io.vlingo.xoom.annotation.autodispatch.HandlerEntry;

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

<#if useCQRS>
import java.util.Collection;
</#if>

public class ${autoDispatchHandlersMappingName} {

  public static final int DEFINE_PLACEHOLDER = 0;
  public static final int ADAPT_STATE = 1;
  <#if useCQRS>
  public static final int QUERY_ALL = 2;
  </#if>

  public static final HandlerEntry<Three<Completes<${stateName}>, Stage, ${dataName}>> defineWithHandler =
         HandlerEntry.of(DEFINE_PLACEHOLDER, (stage, data) -> ${aggregateProtocolName}.definePlaceholder(stage, data.placeholderValue));

  public static final HandlerEntry<Two<${dataName}, ${stateName}>> adaptStateHandler =
          HandlerEntry.of(ADAPT_STATE, ${dataName}::from);

  <#if useCQRS>
  public static final HandlerEntry<Two<Completes<Collection<${dataName}>>, ${queriesName}>> queryAllHandler =
         HandlerEntry.of(QUERY_ALL, ${queriesName}::${queryAllMethodName});
  </#if>

}