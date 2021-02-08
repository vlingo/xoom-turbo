namespace ${packageName};

<#if eventSourced>
public class ${stateName} {
<#else>
using io.vlingo.symbio.store.object.StateObject;

public class ${stateName} : StateObject {
</#if>

  <#list members as member>
  ${member}
  </#list>

  public static ${stateName} identifiedBy(${idType} id) {
    return new ${stateName}(${methodInvocationParameters});
  }

  public ${stateName} (${constructorParameters}) {
    <#list membersAssignment as assignment>
    ${assignment}
    </#list>
  }

  <#list methods as method>
  ${method}
  </#list>
}
