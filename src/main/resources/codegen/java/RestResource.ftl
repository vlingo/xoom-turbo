package ${packageName};

<#if modelProtocol?has_content>
import io.vlingo.actors.Definition;
import io.vlingo.actors.Address;
</#if>
<#if useAutoDispatch>
import io.vlingo.actors.Logger;
import io.vlingo.xoom.annotation.autodispatch.Handler;
</#if>
import io.vlingo.actors.Grid;
import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;
import io.vlingo.http.ContentType;
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
  private final Grid $stage;
  private final Logger $logger;
  <#else>
  private final Grid grid;
  </#if>
  <#if queries?has_content && !queries.empty>
  private final ${queries.protocolName} $queries;
  </#if>

  public ${resourceName}(final Grid grid<#if queries?has_content && !queries.empty>, final ${queries.protocolName} ${queries.attributeName}</#if>) {
    super(grid.world().stage());
    <#if useAutoDispatch>
    this.$stage = grid;
    this.$logger = super.logger();
    <#else>
    this.grid = grid;
    </#if>
    <#if queries?has_content && !queries.empty>
    this.$queries = ${queries.attributeName};
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

  @Override
  protected ContentType contentType() {
    return ContentType.of("application/json", "charset=UTF-8");
  }

  private String location(final String id) {
    return "${uriRoot?replace("/$", "")}/" + id;
  }

  <#if modelProtocol?has_content>
  private Completes<${modelProtocol}> resolve(final String id) {
    <#if useAutoDispatch>
    final Address address = $stage.addressFactory().from(id);
    return $stage.actorOf(${modelProtocol}.class, address, Definition.has(${modelActor}.class, Definition.parameters(id)));
    <#else>
    final Address address = grid.addressFactory().from(id);
    return grid.actorOf(${modelProtocol}.class, address, Definition.has(${modelActor}.class, Definition.parameters(id)));
    </#if>
  }
  </#if>

}
