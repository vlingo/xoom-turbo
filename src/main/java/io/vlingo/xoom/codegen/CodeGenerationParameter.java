// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen;

public enum CodeGenerationParameter {

    APPLICATION_NAME("appName"),
    PACKAGE("package"),
    TARGET_FOLDER("target.folder"),
    AGGREGATES("aggregates"),
    STORAGE_TYPE("storage.type"),
    CQRS("cqrs"),
    ANNOTATIONS("annotations"),
    PROJECTIONS("projections"),
    DATABASE("database"),
    REST_RESOURCES("resources"),
    COMMAND_MODEL_DATABASE("command.model.database"),
    QUERY_MODEL_DATABASE("query.model.database"),
    GENERATION_LOCATION("generation.location");

    private final String key;

    CodeGenerationParameter(final String key) {
        this.key = key;
    }

}
