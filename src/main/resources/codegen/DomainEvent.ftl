package ${packageName};

import io.vlingo.lattice.model.DomainEvent;

public final class ${domainEventName} extends DomainEvent {

  <#list members as member>
  ${member}
  </#list>

  public ${domainEventName}(final ${stateName} state) {
    <#list membersAssignment as assignment>
    ${assignment}
    </#list>
  }
}
