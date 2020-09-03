public Completes<Response> ${methodSignature} {
  return ${queriesAttribute}.greetings()
          .andThenTo(data -> Completes.withSuccess(Response.of(Ok, serialized(data))))
          .recoveryFrom(e -> Completes.withSuccess(Response.of(Failure, e.getMessage())));
}
