package ${packageName};

public enum ${eventTypesName} {
<#list eventsNames as name>
  ${name},
</#list>
}
