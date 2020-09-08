package ${packageName};

import io.vlingo.common.Completes;
import io.vlingo.xoom.annotation.autodispatch.*;
import io.vlingo.xoom.annotation.model.Dummy;

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

import static io.vlingo.http.Method.POST;
<#if useCQRS>
import static io.vlingo.http.Method.GET;

@Queries(protocol = ${queriesName}.class, actor = ${queriesActorName}.class)
</#if>
@Model(protocol = ${aggregateProtocolName}.class, actor = ${entityName}.class, data = ${dataName}.class)
public interface ${autoDispatchMappingName} {

    @Route(method = POST, path="/${uriRoot}", handler = "definePlaceholder(stage, data.placeholderValue)")
    @Response(data = "${dataName}.from")
    Completes<Response> definePlaceholder(@Body ${dataName} data);

    <#if useCQRS>
    @Route(method = GET, path = "/${uriRoot}", handler="${queryAllMethodName}")
    Completes<Response> queryAll();
    </#if>

}