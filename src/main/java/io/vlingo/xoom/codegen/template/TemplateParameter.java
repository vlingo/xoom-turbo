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
    ENTITY_DATA_QUALIFIED_CLASS_NAME("dataQualifiedName"),
    IMPORTS("imports"),
    CONFIGURABLE("configurable"),
    CUSTOM_INITIALIZATION("customInitialization"),
    DATABASE_TYPE("databaseType"),
    ADAPTERS("adapters"),
    STORAGE_PROVIDER_NAME("storeProviderName"),
    STORE_NAME("storeClassName"),
    STATE_NAME("stateName"),
    SOURCE_NAME("sourceName"),
    ADAPTER_NAME("adapterName"),
    STATE_QUALIFIED_CLASS_NAME("stateQualifiedClassName"),
    STORAGE_TYPE("storageType"),
    REQUIRE_ADAPTERS("requireAdapters"),
    STORAGE_DELEGATE_NAME("storageDelegateName"),
    PROJECTIONS("projections"),
    PROJECTION_NAME("projectionName"),
    PROJECTION_TYPE("projectionType"),
    PROJECTION_TO_DESCRIPTION("projectToDescriptions"),
    USE_CQRS("useCQRS"),
    USE_PROJECTIONS("useProjections"),
    USE_ANNOTATIONS("useAnnotations"),
    MODEL_CLASSIFICATION("model"),
    STORE_ATTRIBUTE_NAME("storeAttributeName"),
    REST_RESOURCE_NAME("resourceName"),
    REST_RESOURCE_PACKAGE("restResourcePackage"),
    REST_RESOURCES("restResources"),
    TYPE_REGISTRIES("registries"),
    XOOM_INITIALIZER_CLASS("xoomInitializerClass"),
    STAGE_INSTANTIATION_VARIABLES("stageInstantiationVariables"),
    PROJECTION_DISPATCHER_PROVIDER_NAME("projectionDispatcherProviderName"),
    PROVIDERS("providers"),
    CONNECTION_URL("connectionUrl");

    public final String key;

    TemplateParameter(String key) {
        this.key = key;
    }

}
