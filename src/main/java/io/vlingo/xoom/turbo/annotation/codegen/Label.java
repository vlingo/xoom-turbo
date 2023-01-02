// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.annotation.codegen;

import io.vlingo.xoom.codegen.parameter.ParameterLabel;

public enum Label implements ParameterLabel {

  ID("id"),
  ID_TYPE("idType"),
  BODY("body"),
  BODY_TYPE("bodyType"),
  APPLICATION_NAME("appName"),
  PACKAGE("package"),
  AGGREGATE("aggregate"),
  STATE_FIELD("stateField"),
  FIELD_TYPE("fieldType"),
  AGGREGATE_METHOD("aggregateMethod"),
  METHOD_PARAMETER("methodParameter"),
  REQUIRE_ENTITY_LOADING("requireEntityLoad"),
  STORAGE_TYPE("storage.type"),
  CQRS("cqrs"),
  PROJECTION_TYPE("projections"),
  PROJECTION_ACTOR("projectionActor"),
  SOURCE("source"),
  BLOCKING_MESSAGING("blocking.messaging"),
  XOOM_INITIALIZER_NAME("xoom.initialization.classname"),
  AUTO_DISPATCH_NAME("autoDispatchName"),
  HANDLERS_CONFIG_NAME("handlersConfigName"),
  MODEL_PROTOCOL("modelProtocol"),
  MODEL_ACTOR("modelActor"),
  MODEL_DATA("modelData"),
  QUERIES_PROTOCOL("queriesProtocol"),
  QUERIES_ACTOR("queriesActor"),
  URI_ROOT("uriRoot"),
  ROUTE_SIGNATURE("routeSignature"),
  ROUTE_PATH("routePath"),
  ROUTE_METHOD("routeMethod"),
  ROUTE_HANDLER_INVOCATION("routeHandlerInvocation"),
  USE_CUSTOM_ROUTE_HANDLER_PARAM("useCustomRouteHandlerParam"),
  ADAPTER_HANDLER_INVOCATION("adapterHandlerIndex"),
  USE_CUSTOM_ADAPTER_HANDLER_PARAM("useCustomAdapterHandlerParam"),
  USE_ADAPTER("useAdapter"),
  INTERNAL_ROUTE_HANDLER("customRoute"),
  USE_ANNOTATIONS("annotations"),
  USE_AUTO_DISPATCH("useAutoDispatch"),
  TARGET_FOLDER("targetFolder"),
  VALUE_OBJECT("valueObject"),
  VALUE_OBJECT_FIELD("valueObjectField");

  @SuppressWarnings("unused")
  private final String key;

  Label(final String key) {
    this.key = key;
  }

}
