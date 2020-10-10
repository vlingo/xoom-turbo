public Completes<${stateName}> ${methodName}(${methodParameters}) {
  return apply(new ${domainEventName}(state), () -> state);
}
