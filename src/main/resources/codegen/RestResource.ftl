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
import static io.vlingo.common.serialization.JsonSerialization.serialized;
import static io.vlingo.http.Response.Status.*;
import static io.vlingo.http.ResponseHeader.*;
import static io.vlingo.http.resource.ResourceBuilder.*;
</#if>

public class ${resourceName} extends ResourceHandler {

  private final Stage stage;
  <#if queries?has_content>
  public final ${queries.protocolName} ${queries.attributeName};
  </#if>

  public ${resourceName}(final Stage stage) {
      this.stage = stage;
      <#if queries?has_content>
      this.${queries.attributeName} = QueryModelStoreProvider.instance().${queries.attributeName};
      </#if>
  }

  <#if useAutoDispatch>
  <#list routeMethods as routeMethod>
  ${routeMethod.sourceCode}
  </#list>
  </#if>

  @Override
  public Resource<?> routes() {
  <#if useAutoDispatch && routes?size == 0>
     return resource("${resourceName}" /*Add Request Handlers here as a second parameter*/);
  <else>
     return resource("${resourceName}",
     <#list routes as route>
        ${routeBuilderMethod}("${routePath}")
         <#list route.parameters as parameter>
            .param(${parameter.type}.class)
         </#list>
         <#if route.bodyType?has_content>
            .body(${route.bodyType}.class)
         </#if>
            .handle(this::${route.methodName})
     </#list>
     );
  </#if>
  }

  <#if useAutoDispatch>
  private String location(final String id) {
      return ${uriRoot} + id;
  }

  private Completes<${modelProtocol}> resolve(final String id) {
    return stage.actorOf(${modelProtocol}.class, stage.addressFactory().from(id), ${modelActor}.class);
  }
  </#if>

}