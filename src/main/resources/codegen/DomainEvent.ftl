package ${packageName};

import java.util.UUID;
import io.vlingo.lattice.model.IdentifiedDomainEvent;

public final class ${domainEventName} extends IdentifiedDomainEvent {

  private final UUID eventId;
  <#list members as member>
  ${member}
  </#list>

  public ${domainEventName}(final ${stateName} state) {
    <#list membersAssignment as assignment>
    ${assignment}
    </#list>
    this.eventId = UUID.randomUUID(); //TODO: Define the event id
  }

  @Override
  public String identity() {
    return eventId.toString();
  }
}
