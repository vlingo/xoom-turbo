package ${packageName};

<#if imports?has_content>
<#list imports as import>
import ${import.qualifiedClassName};
</#list>
</#if>

public interface ${aggregateProtocolName} {

  <#list methods as method>
  ${method}
  </#list>
  <#if !useCQRS>
  /*
   * Returns my current state.
   *
   * @return {@code Completes<${stateName}>}
   */
  Completes<${stateName}> currentState();
  </#if>
}