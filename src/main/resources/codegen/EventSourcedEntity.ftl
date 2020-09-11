package ${packageName};

import java.util.Collections;
import java.util.List;

import io.vlingo.common.Completes;
import io.vlingo.lattice.model.sourcing.EventSourced;

public final class ${entityName} extends EventSourced implements ${aggregateProtocolName} {
  private ${stateName} state;

  public ${entityName}(final String id) {
    super(id);
    this.state = ${stateName}.identifiedBy(id);
  }

  public Completes<${stateName}> definePlaceholder(final String value) {
    return apply(new ${domainEventName}(state.id, value), () -> state);
  }

  //=====================================
  // EventSourced
  //=====================================

  static {
    EventSourced.registerConsumer(${entityName}.class, ${domainEventName}.class, ${entityName}::apply${domainEventName});
  }

  private void apply${domainEventName}(final ${domainEventName} e) {
    state = state.withPlaceholderValue(e.placeholderValue);
  }
}
