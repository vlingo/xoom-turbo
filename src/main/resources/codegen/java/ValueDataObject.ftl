package ${packageName};

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