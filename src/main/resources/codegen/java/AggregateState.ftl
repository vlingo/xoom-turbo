package ${packageName};

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

<#if eventSourced>
public final class ${stateName} {
<#else>
import io.vlingo.symbio.store.object.StateObject;

/**
 * See <a href="https://docs.vlingo.io/vlingo-symbio/object-storage">Object Storage</a>
 */
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
