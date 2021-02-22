<#if factoryMethod>
public companion object{
  val ${indexName}_HANDLER: HandlerEntry<Three<Completes<${stateName}>, Stage, ${dataName}>> =
    HandlerEntry.of(${indexName}, ($stage, data) -> ${aggregateProtocolName}.${methodName}(${methodInvocationParameters}))
}
<#else>
public companion object{
  val ${indexName}_HANDLER: HandlerEntry<Three<Completes<${stateName}>, ${aggregateProtocolName}, ${dataName}>> =
    HandlerEntry.of(${indexName}, (${aggregateProtocolVariable}, data) -> ${aggregateProtocolVariable}.${methodName}(${methodInvocationParameters}))
}
</#if>
