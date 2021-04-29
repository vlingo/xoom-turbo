@Override
  public Completes<${stateName}> ${methodName}(${methodParameters}) {
    /**
     * TODO: Implement command logic. See {@link ${stateName}#${methodName}()}
     */
    final ${stateName} stateArg = state.${methodName}(${methodInvocationParameters});
    return apply(new ${domainEventName}(stateArg), () -> state);
  }
