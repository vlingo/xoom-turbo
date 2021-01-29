// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.storage;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import io.vlingo.symbio.store.common.jdbc.Configuration;
import io.vlingo.xoom.ApplicationProperty;

public class DatabaseParameters {

    @SuppressWarnings("unused")
    private static final String XOOM_PREFIX = "VLINGO_XOOM";
    private static final String QUERY_MODEL_PREFIX = "query";
    private static final String COMBINATION_PATTERN = "%s.%s";
    private static final List<String> PROPERTIES_KEYS =
            Arrays.asList("database", "database.name", "database.driver", "database.url",
                    "database.username", "database.password", "database.originator");

    public final Model model;
    public final String database;
    public final String name;
    public final String driver;
    public final String url;
    public final String username;
    public final String password;
    public final String originator;
    public final List<String> keys;
    public final boolean autoCreate;

    public DatabaseParameters(final Model model, final Properties properties) {
        this(model, properties, true);
    }

    public DatabaseParameters(final Model model,
                              final Properties properties,
                              final boolean autoCreate) {
        this.model = model;
        this.keys = prepareKeys();
        this.database = valueFromIndex(0, properties);
        this.name = valueFromIndex(1, properties);
        this.driver = valueFromIndex(2, properties);
        this.url = valueFromIndex(3, properties);
        this.username = valueFromIndex(4, properties);
        this.password = valueFromIndex(5, properties);
        this.originator = valueFromIndex(6, properties);
        this.autoCreate = autoCreate;
    }

    private String valueFromIndex(final Integer index, final Properties properties) {
        return ApplicationProperty.readValue(keys.get(index), properties);
    }

    private void validate() {
        if(database == null) {
            throw new DatabaseParameterNotFoundException(model);
        }
        if(!database.equalsIgnoreCase(Database.IN_MEMORY.name())) {
            if(name == null) {
                throw new DatabaseParameterNotFoundException(model, "name");
            }
            if(driver == null) {
                throw new DatabaseParameterNotFoundException(model, "driver");
            }
            if(url == null) {
                throw new DatabaseParameterNotFoundException(model, "url");
            }
            if(username == null) {
                throw new DatabaseParameterNotFoundException(model, "username");
            }
            if(originator == null) {
                throw new DatabaseParameterNotFoundException(model, "originator");
            }
        }
    }

    private List<String> prepareKeys() {
        return PROPERTIES_KEYS.stream().map(key -> {
            if(model.isQueryModel()) {
                return String.format(COMBINATION_PATTERN, QUERY_MODEL_PREFIX, key);
            }
            return key;
        }).collect(Collectors.toList());
    }

    public Configuration mapToConfiguration() {
        validate();
        return Database.from(database).mapper.apply(this);
    }
}
