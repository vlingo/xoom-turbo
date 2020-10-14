public Completes<Response> ${routeSignature} {
    return resolve(${idName})
            .andThenTo(${modelAttribute} -> ${routeHandlerInvocation})
            .andThenTo(state -> Completes.withSuccess(Response.of(Ok, serialized(${adapterHandlerInvocation}))))
            .otherwise(noGreeting -> Response.of(NotFound, location(${idName})))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }
