public fun ${routeSignature}: Completes<Response> {
    return ${routeHandlerInvocation}
      .andThenTo(state -> Completes.withSuccess(Response.of(Created, headers(of(Location, location(state.id))), serialized(${adapterHandlerInvocation})))
      .otherwise(arg -> Response.of(NotFound, location()))
      .recoverFrom(e -> Response.of(InternalServerError, e.getMessage())))
  }
