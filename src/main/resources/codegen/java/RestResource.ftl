package ${packageName};

<#if modelProtocol?has_content>
import io.vlingo.actors.Definition;
</#if>
<#if useAutoDispatch>
import io.vlingo.actors.Logger;
import io.vlingo.xoom.annotation.autodispatch.Handler;
</#if>
import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;
import io.vlingo.http.Response;
import io.vlingo.http.ResponseHeader;
import io.vlingo.http.resource.Resource;
import io.vlingo.http.resource.DynamicResourceHandler;
<#list imports as import>
import ${import.qualifiedClassName};
</#list>

import static io.vlingo.common.serialization.JsonSerialization.serialized;
import static io.vlingo.http.Response.Status.*;
import static io.vlingo.http.ResponseHeader.Location;
import static io.vlingo.http.resource.ResourceBuilder.resource;

/**
 * See <a href="https://docs.vlingo.io/vlingo-xoom/xoom-annotations#resourcehandlers">@ResourceHandlers</a>
 */
<#if useAutoDispatch>
public class ${resourceName} extends DynamicResourceHandler implements ${autoDispatchMappingName} {
<#else>
public class ${resourceName} extends DynamicResourceHandler {
</#if>
  <#if useAutoDispatch>
  private final Stage $stage;
  private final Logger $logger;
  </#if>
  <#if queries?has_content && !queries.empty>
  private final ${queries.protocolName} $queries;
  </#if>

  public ${resourceName}(final Stage stage) {
      super(stage);
      <#if useAutoDispatch>
      this.$stage = super.stage();
      this.$logger = super.logger();
      </#if>
      <#if queries?has_content && !queries.empty>
      this.$queries = ${storeProviderName}.instance().${queries.attributeName};
      </#if>
  }

  <#list routeMethods as routeMethod>
  ${routeMethod}
  </#list>
  @Override
  public Resource<?> routes() {
  <#if routeDeclarations?has_content && routeDeclarations?size == 0>
     return resource("${resourceName}" /*Add Request Handlers here as a second parameter*/);
  <#else>
     return resource("${resourceName}",
     <#list routeDeclarations as declaration>
        <#if declaration.path?has_content>
        ${declaration.builderMethod}("${declaration.path}")
        <#else>
        ${declaration.builderMethod}("${uriRoot}")
        </#if>
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

  private String location() {
    return location("");
  }

  private String location(final String id) {
    return "${uriRoot?replace("/$", "")}/" + id;
  }

  <#if modelProtocol?has_content>
  private Completes<${modelProtocol}> resolve(final String id) {
    return stage().actorOf(${modelProtocol}.class, stage().addressFactory().from(id), Definition.has(${modelActor}.class, Definition.parameters(id)));
  }
  </#if>

}
