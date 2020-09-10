public Completes<Response> ${routeSignature} {
  return ${queriesAttribute}.${routeHandler}
          .andThenTo(data -> Completes.withSuccess(Response.of(Ok, serialized(data))))
          .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
}
