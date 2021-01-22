package ${packageName};

import java.util.UUID;
import io.vlingo.common.version.SemanticVersion;
import io.vlingo.lattice.model.IdentifiedDomainEvent;

public final class ${domainEventName} extends IdentifiedDomainEvent {

  private final UUID eventId;
  <#list members as member>
  ${member}
  </#list>

  public ${domainEventName}(final ${stateName} state) {
    super(SemanticVersion.from("0.0.1").toValue());
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
