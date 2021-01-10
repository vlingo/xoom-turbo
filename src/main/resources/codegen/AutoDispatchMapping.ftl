package ${packageName};

import io.vlingo.common.Completes;
import io.vlingo.xoom.annotation.autodispatch.*;
import io.vlingo.http.Response;

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

import static io.vlingo.http.Method.*;

@AutoDispatch(path="/${uriRoot}", handlers=${autoDispatchHandlersMappingName}.class)
<#if useCQRS>
@Queries(protocol = ${queriesName}.class, actor = ${queriesActorName}.class)
</#if>
@Model(protocol = ${aggregateProtocolName}.class, actor = ${entityName}.class, data = ${dataName}.class)
public interface ${autoDispatchMappingName} {

  <#list routeDeclarations as declaration>
  ${declaration}
  </#list>
}