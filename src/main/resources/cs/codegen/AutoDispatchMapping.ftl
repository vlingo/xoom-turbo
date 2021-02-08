namespace ${packageName};

using io.vlingo.common.Completes;
using io.vlingo.xoom.annotation.autodispatch.*;
using io.vlingo.http.Response;

<#list usings as using>
using ${using.qualifiedClassName};
</#list>

using static io.vlingo.http.Method.*;

@AutoDispatch(path="${uriRoot}", handlers=${autoDispatchHandlersMappingName}.class)
<#if useCQRS>
@Queries(protocol = ${queriesName}.class, actor = ${queriesActorName}.class)
</#if>
@Model(protocol = ${aggregateProtocolName}.class, actor = ${entityName}.class, data = ${dataName}.class)
public interface ${autoDispatchMappingName} {

  <#list routeDeclarations as declaration>
  ${declaration}
  </#list>
}