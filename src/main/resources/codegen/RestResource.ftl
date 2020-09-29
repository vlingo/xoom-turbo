package ${packageName};

import io.vlingo.actors.Stage;
import io.vlingo.http.resource.Resource;
import io.vlingo.http.resource.ResourceHandler;
import static io.vlingo.http.resource.ResourceBuilder.resource;

<#if useAutoDispatch>
<#list imports as import>
import ${import.qualifiedClassName};
</#list>

import io.vlingo.http.Response;
import io.vlingo.common.Completes;
import io.vlingo.xoom.annotation.autodispatch.Handler;
import static io.vlingo.common.serialization.JsonSerialization.serialized;
import static io.vlingo.http.Response.Status.*;
import static io.vlingo.http.ResponseHeader.*;
import static io.vlingo.http.resource.ResourceBuilder.*;
</#if>

<#if useAutoDispatch>
public class ${resourceName} extends ResourceHandler implements ${autoDispatchMappingName} {
<#else>
public class ${resourceName} extends ResourceHandler {
</#if>

  private final Stage $stage;
  <#if queries?has_content && !queries.empty>
  private final ${queries.protocolName} $queries;
  </#if>

  public ${resourceName}(final Stage $stage) {
      this.$stage = $stage;
      <#if queries?has_content && !queries.empty>
      this.$queries = ${storeProviderName}.instance().${queries.attributeName};
      </#if>
  }

  <#if useAutoDispatch>
  <#list routeMethods as routeMethod>
  ${routeMethod}
  </#list>
  </#if>

  @Override
  public Resource<?> routes() {
  <#if !useAutoDispatch || (routeDeclarations?has_content && routeDeclarations?size == 0)>
     return resource("${resourceName}" /*Add Request Handlers here as a second parameter*/);
  <#else>
     return resource("${resourceName}",
     <#list routeDeclarations as declaration>
        ${declaration.builderMethod}("${declaration.path}")
         <#list declaration.parameterTypes as parameterType>
            .param(${parameterType}.class)
         </#list>
         <#if declaration.bodyType?has_content>
            .body(${declaration.bodyType}.class)
         </#if>
         <#if declaration.last>
            .handle(this::${declaration.handlerName})
         <#else>
            .handle(this::${declaration.handlerName}),
         </#if>
     </#list>
     );
  </#if>
  }

  <#if useAutoDispatch>
  private String location() {
    return location(null);
  }

  private String location(final String id) {
    return id== null ? "${uriRoot}" : "${uriRoot}" + id;
  }

  <#if modelProtocol?has_content>
  private Completes<${modelProtocol}> resolve(final String id) {
    return $stage.actorOf(${modelProtocol}.class, $stage.addressFactory().from(id), ${modelActor}.class);
  }
  </#if>
  </#if>

}