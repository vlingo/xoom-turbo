package ${packageName};

import io.vlingo.xoom.annotation.persistence.Persistence;
import io.vlingo.xoom.annotation.persistence.Persistence.StorageType;
<#if useProjections>
import io.vlingo.xoom.annotation.persistence.Projections;
import io.vlingo.xoom.annotation.persistence.Projection;
</#if>
<#if requireAdapters>
import io.vlingo.symbio.State.TextState;
import io.vlingo.xoom.annotation.persistence.StateAdapters;
import io.vlingo.xoom.annotation.persistence.StateAdapter;
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
@StateAdapters({
<#list adapters as stateAdapter>
  <#if stateAdapter.last>
  @StateAdapter(from = ${stateAdapter.sourceClass}.class, to = TextState.class)
  <#else>
  @StateAdapter(from = ${stateAdapter.sourceClass}.class, to = TextState.class),
  </#if>
</#list>
})
</#if>
public class ${storeProviderName} {


}