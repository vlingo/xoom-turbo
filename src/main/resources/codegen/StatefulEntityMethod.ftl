public Completes<${stateName}> ${methodName}(${methodParameters}) {
    state = state.${methodName}(${methodInvocationParameters});
    return apply(state, new ${domainEventName}(state), () -> state);
  }
