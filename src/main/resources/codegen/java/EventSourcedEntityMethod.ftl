@Override
  public Completes<${stateName}> ${methodName}(${methodParameters}) {
    final ${stateName} stateArg = state.${methodName}(${methodInvocationParameters});
    return apply(new ${domainEventName}(stateArg), () -> state);
  }
