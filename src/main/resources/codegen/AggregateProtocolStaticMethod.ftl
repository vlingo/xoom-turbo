static Completes<${stateName}> ${methodName}(${methodParameters}) {
    final Address address = stage.world().addressFactory().uniquePrefixedWith("g-");
    final ${aggregateProtocolName} ${aggregateProtocolVariable} = stage.actorFor(${aggregateProtocolName}.class, Definition.has(${entityName}.class, Definition.parameters(address.idString())), address);
    return ${aggregateProtocolVariable}.${methodName}(${methodInvocationParameters});
  }
