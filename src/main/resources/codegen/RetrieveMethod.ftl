public Completes<Response> ${routeSignature} {
  return ${queriesAttribute}.${routeHandler}
          .andThenTo(data -> Completes.withSuccess(Response.of(Ok, serialized(data))))
          .otherwise(arg -> Response.of(NotFound, location()))
          .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
}
