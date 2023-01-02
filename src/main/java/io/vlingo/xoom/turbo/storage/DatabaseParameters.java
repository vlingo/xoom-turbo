// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.storage;

import io.vlingo.xoom.symbio.store.StorageException;
import io.vlingo.xoom.symbio.store.common.jdbc.Configuration;
import io.vlingo.xoom.turbo.ApplicationProperty;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class DatabaseParameters {

  @SuppressWarnings("unused")
  private static final String XOOM_PREFIX = "VLINGO_XOOM";
  private static final String QUERY_MODEL_PREFIX = "query";
  private static final String COMBINATION_PATTERN = "%s.%s";
  private static final List<String> PROPERTIES_KEYS =
          Arrays.asList("database", "database.name", "database.driver", "database.url",
                  "database.username", "database.password", "database.originator", "database.connection.attempts");

  public final Model model;
  public final String database;
  public final String name;
  public final String driver;
  public final String url;
  public final String username;
  public final String password;
  public final String originator;
  public final String attempts;
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
    this.attempts = valueFromIndex(7, properties);
    this.autoCreate = autoCreate;
  }

  private String valueFromIndex(final Integer index, final Properties properties) {
    return ApplicationProperty.readValue(keys.get(index), properties);
  }

  private void validate() {
    if (database == null) {
      throw new DatabaseParameterNotFoundException(model);
    }
    if (!database.equalsIgnoreCase(Database.IN_MEMORY.name())) {
      if (name == null) {
        throw new DatabaseParameterNotFoundException(model, "name");
      }
      if (driver == null) {
        throw new DatabaseParameterNotFoundException(model, "driver");
      }
      if (url == null) {
        throw new DatabaseParameterNotFoundException(model, "url");
      }
      if (username == null) {
        throw new DatabaseParameterNotFoundException(model, "username");
      }
      if (originator == null) {
        throw new DatabaseParameterNotFoundException(model, "originator");
      }
      if (attempts != null && !NumberUtils.isCreatable(attempts)) {
        throw new IllegalArgumentException("Error reading database settings. " +
                "The value of connection attempts must be a number");
      }
    }
  }

  private List<String> prepareKeys() {
    return PROPERTIES_KEYS.stream().map(key -> {
      if (model.isQueryModel()) {
        return String.format(COMBINATION_PATTERN, QUERY_MODEL_PREFIX, key);
      }
      return key;
    }).collect(Collectors.toList());
  }

  public Configuration mapToConfiguration() {
    try {
      validate();
      return tryToCreateConfiguration();
    } catch (final InterruptedException e) {
      throw new StorageException(null, "Unable to connect to the database", e);
    }
  }

  private Configuration tryToCreateConfiguration() throws InterruptedException {
    int failedAttempts = 1;
    StorageException connectionException = null;
    final Integer maximumAttempts = attempts == null ? 1 : Integer.valueOf(attempts);
    while (failedAttempts <= maximumAttempts) {
      try {
        System.out.println("Database connection attempt #" + failedAttempts);
        return Database.from(database).mapper.apply(this);
      } catch (final StorageException exception) {
        connectionException = exception;
        Thread.sleep(2000);
        failedAttempts++;
      }
    }
    throw connectionException;
  }


}
