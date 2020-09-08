package ${packageName};

public interface ${aggregateProtocolName} {

    static Completes<${stateName}> definePlaceholder(final Stage stage, final String placeholderValue) {
      final Address address = stage.world().addressFactory().uniquePrefixedWith("g-");
      final ${aggregateProtocolName} ${aggregateProtocolVariable} = stage.actorFor(${aggregateProtocolName}.class, Definition.has(${entityName}.class, Definition.parameters(address.idString())), address);
      return ${aggregateProtocolVariable}.definePlaceholder(placeholderValue);
    }

}