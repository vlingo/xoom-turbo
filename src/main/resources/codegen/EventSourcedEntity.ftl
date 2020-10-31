package ${packageName};

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

import io.vlingo.lattice.model.sourcing.EventSourced;

public final class ${entityName} extends EventSourced implements ${aggregateProtocolName} {
  private ${stateName} state;

  public ${entityName}(final ${idType} id) {
    super(id);
    this.state = ${stateName}.identifiedBy(id);
  }

  <#if sourcedEvents?has_content>
  static {
    <#list sourcedEvents as sourcedEvent>
    EventSourced.registerConsumer(${entityName}.class, ${sourcedEvent}.class, ${entityName}::apply${sourcedEvent});
    </#list>
  }
  </#if>

  <#list methods as method>
  ${method}
  </#list>
  <#list sourcedEvents as sourcedEvent>
  private void apply${sourcedEvent}(final ${sourcedEvent} event) {
    //TODO: Handle ${sourcedEvent} here
  }

  </#list>
}
