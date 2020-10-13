public Completes<${stateName}> ${methodName}(${methodParameters}) {
    return apply(state.${methodName}(${methodInvocationParameters}), new ${domainEventName}(state), () -> state);
  }
