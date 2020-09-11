package ${packageName};

import io.vlingo.actors.Address;
import io.vlingo.actors.Definition;
import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;

public interface ${aggregateProtocolName} {

  static Completes<${stateName}> definePlaceholder(final Stage stage, final String placeholderValue) {
    final Address address = stage.world().addressFactory().uniquePrefixedWith("g-");
    final ${aggregateProtocolName} ${aggregateProtocolVariable} = stage.actorFor(${aggregateProtocolName}.class, Definition.has(${entityName}.class, Definition.parameters(address.idString())), address);
    return ${aggregateProtocolVariable}.definePlaceholder(placeholderValue);
  }

  Completes<${stateName}> definePlaceholder(final String placeholderValue);

}