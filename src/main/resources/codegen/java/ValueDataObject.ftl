package ${packageName};

<#if imports?has_content>
<#list imports as import>
import ${import.qualifiedClassName};
</#list>
</#if>

public class ${dataValueObjectName} {

  <#list members as member>
  ${member}
  </#list>

  public static ${dataValueObjectName} of(${constructorParameters}) {
    return new ${dataValueObjectName}(${constructorInvocationParameters});
  }

  private ${dataValueObjectName} (${constructorParameters}) {
    <#list membersAssignment as assignment>
    ${assignment}
    </#list>
  }

}