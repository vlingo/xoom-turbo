package ${packageName};

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

public interface ${aggregateProtocolName} {

  <#list methods as method>
  ${method}
  </#list>

}