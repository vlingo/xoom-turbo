package ${packageName};

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

import io.vlingo.lattice.model.sourcing.EventSourced;

public final class ${entityName} extends EventSourced implements ${aggregateProtocolName} {
  private ${stateName} state;

  public ${entityName}(final {String} id) {
    super(id);
    this.state = ${stateName}.identifiedBy(id);
  }

  <#if sourcedEvents?has_content && !sourcedEvents.empty>
  static {
    <#list sourcedEvents as sourcedEvent>
    EventSourced.registerConsumer(${entityName}.class, ${domainEventName}.class, ${entityName}::apply${domainEventName});
    </#list>
  }
  </#if>

  <#list sourcedEvents as sourcedEvent>
  private void apply${domainEventName}(final ${domainEventName} event) {
    //TODO: Handle ${domainEventName} here
  }
  </#list>
}
