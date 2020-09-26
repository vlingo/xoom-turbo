package ${packageName};

import io.vlingo.common.Completes;
import io.vlingo.xoom.annotation.autodispatch.*;
import io.vlingo.http.Response;

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

import static io.vlingo.http.Method.POST;
<#if useCQRS>
import static io.vlingo.http.Method.GET;
</#if>

@AutoDispatch(path="/${uriRoot}", handlers=${autoDispatchHandlersMappingName}.class)
<#if useCQRS>
@Queries(protocol = ${queriesName}.class, actor = ${queriesActorName}.class)
</#if>
@Model(protocol = ${aggregateProtocolName}.class, actor = ${entityName}.class, data = ${dataName}.class)
public interface ${autoDispatchMappingName} {

    @Route(method = POST, handler = ${autoDispatchHandlersMappingName}.DEFINE_PLACEHOLDER)
    @ResponseAdapter(handler = ${autoDispatchHandlersMappingName}.ADAPT_STATE)
    Completes<Response> definePlaceholder(@Body ${dataName} data);

    <#if useCQRS>
    @Route(method = GET, handler = ${autoDispatchHandlersMappingName}.QUERY_ALL)
    Completes<Response> queryAll();
    </#if>

}