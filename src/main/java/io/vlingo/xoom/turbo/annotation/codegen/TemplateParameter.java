// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.codegen;

import io.vlingo.xoom.codegen.template.ParameterKey;

public enum TemplateParameter implements ParameterKey {

  APPLICATION_NAME("appName"),
  AUTO_DISPATCH_MAPPING_NAME("autoDispatchMappingName"),
  AGGREGATES("aggregates"),
  BLOCKING_MESSAGING("blockingMessaging"),
  STATE_DATA_OBJECT_NAME("dataName"),
  CUSTOM_INITIALIZATION("customInitialization"),
  ADAPTERS("adapters"),
  STORE_PROVIDER_NAME("storeProviderName"),
  STATE_NAME("stateName"),
  SOURCE_NAME("sourceName"),
  ADAPTER_NAME("adapterName"),
  STORAGE_TYPE("storageType"),
  REQUIRE_ADAPTERS("requireAdapters"),
  PROJECTION_TYPE("projectionType"),
  ID_NAME("idName"),
  ROUTE_DECLARATIONS("routeDeclarations"),
  ROUTE_SIGNATURE("routeSignature"),
  ROUTE_METHOD("routeMethod"),
  ROUTE_METHODS("routeMethods"),
  ROUTE_HANDLER_INVOCATION("routeHandlerInvocation"),
  REQUIRE_ENTITY_LOADING("requireEntityLoading"),
  ADAPTER_HANDLER_INVOCATION("adapterHandlerInvocation"),
  HANDLERS_CONFIG_NAME("handlersConfigName"),
  QUERIES("queries"),
  QUERIES_NAME("queriesName"),
  QUERIES_ACTOR_NAME("queriesActorName"),
  PROJECTION_TO_DESCRIPTION("projectToDescriptions"),
  PERSISTENT_TYPES("persistentTypes"),
  USE_CQRS("useCQRS"),
  USE_PROJECTIONS("useProjections"),
  USE_ANNOTATIONS("useAnnotations"),
  USE_AUTO_DISPATCH("useAutoDispatch"),
  MODEL("model"),
  LOCATION_PATH("locationPath"),
  URI_ROOT("uriRoot"),
  VALUE_OBJECT_INITIALIZERS("valueObjectInitializers"),
  MODEL_ACTOR("modelActor"),
  MODEL_PROTOCOL("modelProtocol"),
  MODEL_ATTRIBUTE("modelAttribute"),
  REST_RESOURCE_NAME("resourceName"),
  REST_RESOURCES("restResources"),
  TYPE_REGISTRIES("registries"),
  XOOM_INITIALIZER_CLASS("xoomInitializerClass"),
  PROJECTION_DISPATCHER_PROVIDER_NAME("projectionDispatcherProviderName"),
  PROVIDERS("providers"),
  EXCHANGE_BOOTSTRAP_NAME("exchangeBootstrapName"),
  COMPOSITE_ID("compositeId");

  public final String key;

  TemplateParameter(String key) {
    this.key = key;
  }

  @Override
  public String value() {
    return key;
  }

}
