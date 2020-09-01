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
    AGGREGATE_PROTOCOL_NAME("aggregateProtocolName"),
    BLOCKING_MESSAGING("blockingMessaging"),
    DOMAIN_EVENT_NAME("domainEventName"),
    PLACEHOLDER_EVENT("placeholderEvent"),
    ENTITY_NAME("entityName"),
    ENTITY_DATA_NAME("dataName"),
    ENTITY_DATA_QUALIFIED_NAME("dataQualifiedName"),
    IMPORTS("imports"),
    CUSTOM_INITIALIZATION("customInitialization"),
    ADAPTERS("adapters"),
    STORAGE_PROVIDER_NAME("storeProviderName"),
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
    PROJECTION_TO_DESCRIPTION("projectToDescriptions"),
    USE_CQRS("useCQRS"),
    USE_PROJECTIONS("useProjections"),
    USE_ANNOTATIONS("useAnnotations"),
    MODEL("model"),
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
