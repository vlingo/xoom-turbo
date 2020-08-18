// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template;

public enum Template {

    ANNOTATED_BOOTSTRAP("AnnotatedBootstrap"),
    DEFAULT_BOOTSTRAP("DefaultBootstrap"),
    AGGREGATE_PROTOCOL("AggregateProtocol"),
    OBJECT_ENTITY("ObjectEntity"),
    STATEFUL_ENTITY("StatefulEntity"),
    EVENT_SOURCE_ENTITY("EventSourcedEntity"),
    STATE_OBJECT("BeanState"),
    PLAIN_STATE("ValueState"),
    DOMAIN_EVENT("DomainEvent"),
    JOURNAL_PROVIDER("JournalProvider"),
    ANNOTATED_STORE_PROVIDER("PersistenceSetup"),
    STATE_STORE_PROVIDER("StateStoreProvider"),
    OBJECT_STORE_PROVIDER("ObjectStoreProvider"),
    QUERY_MODEL_OBJECT_STORE_PROVIDER("QueryModelObjectStoreProvider"),
    STORE_PROVIDER_CONFIGURATION("StoreProviderConfiguration"),
    STORAGE_CONFIGURATION("StorageConfiguration"),
    PLACEHOLDER_DOMAIN_EVENT("PlaceholderDefinedEvent"),
    ENTRY_ADAPTER("EntryAdapter"),
    STATE_ADAPTER("StateAdapter"),
    REST_RESOURCE("RestResource"),
    XOOM_INITIALIZER("XoomInitializer"),
    PROJECTION_DISPATCHER_PROVIDER("ProjectionDispatcherProvider"),
    OPERATION_BASED_PROJECTION("OperationBasedProjection"),
    EVENT_BASED_PROJECTION("EventBasedProjection"),
    ENTITY_DATA("ValueData"),
    EVENT_TYPES("EventTypes"),
    DATABASE_PROPERTIES("DatabaseProperties");

    public final String filename;

    Template(final String filename) {
        this.filename = filename;
    }

}
