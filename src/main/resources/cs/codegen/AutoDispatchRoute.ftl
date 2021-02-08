<#if routePath?has_content>
@Route(method = ${routeMethod}, path = "${routePath}", handler = ${autoDispatchHandlersMappingName}.${routeMappingValue})
<#else>
@Route(method = ${routeMethod}, handler = ${autoDispatchHandlersMappingName}.${routeMappingValue})
</#if>
<#if retrievalRoute>
  Completes<Response> ${methodName}();
<#else>
  @ResponseAdapter(handler = ${autoDispatchHandlersMappingName}.ADAPT_STATE)
  <#if requireEntityLoading>
  Completes<Response> ${methodName}(@Id ${idType} id, @Body ${dataName} data);
  <#else>
  Completes<Response> ${methodName}(@Body ${dataName} data);
  </#if>
</#if>
