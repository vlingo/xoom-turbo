// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template;

public enum TemplateParameter {

    BASE_PACKAGE("basePackage"),
    PACKAGE_NAME("packageName"),
    APPLICATION_NAME("appName"),
    AUTO_DISPATCH_MAPPING_NAME("autoDispatchMappingName"),
    AGGREGATE_PROTOCOL_NAME("aggregateProtocolName"),
    AGGREGATE_PROTOCOL_VARIABLE("aggregateProtocolVariable"),
    BLOCKING_MESSAGING("blockingMessaging"),
    DOMAIN_EVENT_NAME("domainEventName"),
    PLACEHOLDER_EVENT("placeholderEvent"),
    ENTITY_NAME("entityName"),
    ENTITY_DATA_NAME("dataName"),
    ENTITY_DATA_QUALIFIED_NAME("dataQualifiedName"),
    IMPORTS("imports"),
    CUSTOM_INITIALIZATION("customInitialization"),
    ADAPTERS("adapters"),
    STORE_PROVIDER_NAME("storeProviderName"),
    STATE_NAME("stateName"),
    EVENTS_NAMES("eventsNames"),
    EVENT_TYPES_NAME("eventTypesName"),
    EVENT_TYPES_QUALIFIED_NAME("eventTypesQualifiedName"),
    SOURCE_NAME("sourceName"),
    ADAPTER_NAME("adapterName"),
    STATE_QUALIFIED_CLASS_NAME("stateQualifiedClassName"),
    STORAGE_TYPE("storageType"),
    REQUIRE_ADAPTERS("requireAdapters"),
    RESOURCE_FILE("resourceFile"),
    PROJECTIONS("projections"),
    PROJECTION_NAME("projectionName"),
    PROJECTION_TYPE("projectionType"),
    ADAPTER_INVOCATION("adapterInvocation"),
    ID_NAME("idName"),
    ROUTE_DECLARATIONS("routeDeclarations"),
    ROUTE_PATH("routePath"),
    ROUTE_SIGNATURE("routeSignature"),
    ROUTE_METHOD("routeMethod"),
    ROUTE_METHODS("routeMethods"),
    ROUTE_HANDLER("routeHandler"),
    QUERIES("queries"),
    QUERIES_NAME("queriesName"),
    QUERIES_ACTOR_NAME("queriesActorName"),
    QUERIES_ATTRIBUTE("queriesAttribute"),
    QUERY_ID_METHOD_NAME("queryByIdMethodName"),
    QUERY_ALL_METHOD_NAME("queryAllMethodName"),
    PROJECTION_TO_DESCRIPTION("projectToDescriptions"),
    USE_CQRS("useCQRS"),
    USE_PROJECTIONS("useProjections"),
    USE_ANNOTATIONS("useAnnotations"),
    USE_AUTO_DISPATCH("useAutoDispatch"),
    MODEL("model"),
    URI_ROOT("uriRoot"),
    MODEL_ACTOR("modelActor"),
    MODEL_PROTOCOL("modelProtocol"),
    REST_RESOURCE_NAME("resourceName"),
    REST_RESOURCE_PACKAGE("restResourcePackage"),
    DEFAULT_DATABASE_PARAMETER("databaseParameter"),
    QUERY_DATABASE_PARAMETER("queryDatabaseParameter"),
    REST_RESOURCES("restResources"),
    TYPE_REGISTRIES("registries"),
    XOOM_INITIALIZER_CLASS("xoomInitializerClass"),
    STAGE_INSTANTIATION_VARIABLES("stageInstantiationVariables"),
    PROJECTION_DISPATCHER_PROVIDER_NAME("projectionDispatcherProviderName"),
    PROVIDERS("providers");

    public final String key;

    TemplateParameter(String key) {
        this.key = key;
    }

}
