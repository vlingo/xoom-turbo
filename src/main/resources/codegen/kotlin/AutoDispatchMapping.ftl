package ${packageName}

import io.vlingo.common.Completes
import io.vlingo.xoom.annotation.autodispatch.*
import io.vlingo.http.Response

<#list imports as import>
import ${import.qualifiedClassName}
</#list>

import io.vlingo.http.Method.*

@AutoDispatch(path="${uriRoot}", handlers=${autoDispatchHandlersMappingName}::class.java)
<#if useCQRS>
@Queries(protocol = ${queriesName}::class.java, actor = ${queriesActorName}::class.java)
</#if>
@Model(protocol = ${aggregateProtocolName}::class.java, actor = ${entityName}::class.java, data = ${dataName}::class.java)
public interface ${autoDispatchMappingName} {

  <#list routeDeclarations as declaration>
  ${declaration}
  </#list>
}
