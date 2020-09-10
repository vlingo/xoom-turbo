public Completes<Response> ${routeSignature} {
  return resolve(${idName})
          .andThenTo(entity -> entity.${routeHandler})
          .andThenTo(state -> Completes.withSuccess(Response.of(Ok, serialized(${adapterInvocation}(state)))))
          .otherwise(noGreeting -> Response.of(NotFound, location(${idName})))
          .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
}
