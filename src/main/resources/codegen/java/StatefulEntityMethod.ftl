@Override
  public Completes<${stateName}> ${methodName}(${methodParameters}) {
    final ${stateName} stateArg = state.${methodName}(${methodInvocationParameters});
    return apply(stateArg, new ${domainEventName}(stateArg), () -> state);
  }
