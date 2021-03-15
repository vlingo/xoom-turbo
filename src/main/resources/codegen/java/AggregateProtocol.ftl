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
}