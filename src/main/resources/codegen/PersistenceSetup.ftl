package ${packageName};

import io.vlingo.xoom.annotation.persistence.Persistence;
import io.vlingo.xoom.annotation.persistence.Persistence.StorageType;
<#if useProjections>
import io.vlingo.xoom.annotation.persistence.Projections;
import io.vlingo.xoom.annotation.persistence.Projection;
</#if>
<#if requireAdapters>
import io.vlingo.xoom.annotation.persistence.StateAdapters;
</#if>
<#list imports as import>
import ${import.qualifiedClassName};
</#list>

@Persistence(basePackage = "${basePackage}", storageType = StorageType.${storageType}, cqrs = ${useCQRS?c})
<#if useProjections>
@Projections({
<#list projections as projection>
  <#if projection.last>
  @Projection(actor = ${projection.actor}.class, becauseOf = {${projection.causes}})
  <#else>
  @Projection(actor = ${projection.actor}.class, becauseOf = {${projection.causes}}),
  </#if>
</#list>
})
</#if>
<#if requireAdapters>
@StateAdapters(states = {
<#list adapters as stateAdapter>
  <#if stateAdapter.last>
  ${stateAdapter.sourceClass}.class
  <#else>
  ${stateAdapter.sourceClass}.class,
  </#if>
</#list>
})
</#if>
public class ${storeProviderName} {


}