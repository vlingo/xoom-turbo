public Completes<Response> ${methodSignature} {
  return ${modelProtocol}
    .${routeHandler}
    .andThenTo(state -> Completes.withSuccess(Response.of(Created, headers(of(Location, location(state.id))), serialized(${adapterInvocation}(state)))))
    .recoveryFrom(e -> Completes.withSuccess(Response.of(Failure, e.getMessage())));
}
