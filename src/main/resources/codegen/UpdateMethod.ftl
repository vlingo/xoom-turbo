public Completes<Response> ${methodSignature} {
  return resolve(${idName})
          .andThenTo(entity -> entity.${routeHandler})
          .andThenTo(state -> Completes.withSuccess(Response.of(Ok, serialized(${adapterInvocation}(state)))))
          .otherwise(noGreeting -> Response.of(NotFound, location(${idName})))
          .recoveryFrom(e -> Completes.withSuccess(Response.of(Failure, e.getMessage())));
}
