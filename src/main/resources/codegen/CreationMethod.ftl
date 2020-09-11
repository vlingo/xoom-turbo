public Completes<Response> ${routeSignature} {
  return ${modelProtocol}
    .${routeHandler}
    .andThenTo(state -> Completes.withSuccess(Response.of(Created, headers(of(Location, location(state.id))), serialized(${adapterInvocation}(state)))))
    .otherwise(arg -> Response.of(NotFound, location()))
    .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
}
