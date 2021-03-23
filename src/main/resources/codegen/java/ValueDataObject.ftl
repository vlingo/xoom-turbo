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

  <#list staticFactoryMethods as factoryMethod>
  public static ${factoryMethod.dataObjectName} from(${factoryMethod.parameters}) {
    return ${factoryMethod.constructorInvocation};
  }

  </#list>
  private ${dataValueObjectName} (${constructorParameters}) {
    <#list membersAssignment as assignment>
    ${assignment}
    </#list>
  }

}