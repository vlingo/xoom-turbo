// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.parameter;

public enum Label {

    APPLICATION_NAME("appName"),
    PACKAGE("package"),
    TARGET_FOLDER("target.folder"),
    AGGREGATES("aggregates"),
    STORAGE_TYPE("storage.type"),
    CQRS("cqrs"),
    ANNOTATIONS("annotations"),
    AUTO_DISPATCH("autoDispatch"),
    PROJECTION_TYPE("projections"),
    PROJECTABLES("projectables"),
    DATABASE("database"),
    REST_RESOURCES("resources"),
    ADDRESS_FACTORY("address.factory"),
    IDENTITY_GENERATOR("identity.generator"),
    BLOCKING_MESSAGING("blocking.messaging"),
    XOOM_INITIALIZER_NAME("xoom.initialization.classname"),
    COMMAND_MODEL_DATABASE("command.model.database"),
    QUERY_MODEL_DATABASE("query.model.database"),
    GENERATION_LOCATION("generation.location"),

    //Auto Dispatch Labels

    ID("id"),
    ID_TYPE("idType"),
    BODY("body"),
    BODY_TYPE("bodyType"),
    AUTO_DISPATCH_NAME("autoDispatchName"),
    MODEL_PROTOCOL("modelProtocol"),
    MODEL_ACTOR("modelActor"),
    MODEL_DATA("modelData"),
    QUERIES_PROTOCOL("queriesProtocol"),
    QUERIES_ACTOR("queriesActor"),
    URI_ROOT("uriRoot"),
    ROUTE_SIGNATURE("routeSignature"),
    ROUTE_PATH("routePath"),
    ROUTE_METHOD("routeMethod"),
    ROUTE_HANDLER("routeHandler"),
    RESPONSE_DATA("responseData"),
    CUSTOM_ROUTE("customRoute"), //boolean
    USE_AUTO_DISPATCH("useAutoDispatch");

    private final String key;

    Label(final String key) {
        this.key = key;
    }

}
