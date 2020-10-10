package ${packageName};

<#if eventSourced>
public final class ${stateName} {
<#else>
import io.vlingo.symbio.store.object.StateObject;

public final class ${stateName} extends StateObject {
</#if>

  <#list members as member>
  ${member}
  </#list>

  public static ${stateName} identifiedBy(final ${idType} id) {
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
