// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.storage;

import io.vlingo.xoom.symbio.store.DataFormat;
import io.vlingo.xoom.symbio.store.Result;
import io.vlingo.xoom.symbio.store.StorageException;
import io.vlingo.xoom.symbio.store.common.jdbc.Configuration;
import io.vlingo.xoom.symbio.store.common.jdbc.hsqldb.HSQLDBConfigurationProvider;
import io.vlingo.xoom.symbio.store.common.jdbc.mysql.MySQLConfigurationProvider;
import io.vlingo.xoom.symbio.store.common.jdbc.postgres.PostgresConfigurationProvider;
import io.vlingo.xoom.symbio.store.common.jdbc.yugabyte.YugaByteConfigurationProvider;

import java.util.function.Function;

public enum Database {

  IN_MEMORY(parameters -> null),

  POSTGRES(parameters ->
  {
    try {
      return PostgresConfigurationProvider.configuration(DataFormat.Text,
              parameters.url, parameters.name, parameters.username,
              parameters.password, parameters.originator, parameters.autoCreate);
    } catch (final Exception e) {
      throw new StorageException(Result.Error, e.getMessage());
    }
  }),

  HSQLDB(parameters ->
  {
    try {
      return HSQLDBConfigurationProvider.configuration(DataFormat.Text,
              parameters.url, parameters.name, parameters.username,
              parameters.password, parameters.originator, true);
    } catch (final Exception e) {
      throw new StorageException(Result.Error, e.getMessage());
    }
  }),

  MYSQL(parameters ->
  {
    try {
      return MySQLConfigurationProvider.configuration(DataFormat.Text,
              parameters.url, parameters.name, parameters.username,
              parameters.password, parameters.originator, true);
    } catch (final Exception e) {
      throw new StorageException(Result.Error, e.getMessage());
    }
  }),

  YUGA_BYTE(parameters ->
  {
    try {
      return YugaByteConfigurationProvider.configuration(DataFormat.Text,
              parameters.url, parameters.name, parameters.username,
              parameters.password, parameters.originator, true);
    } catch (final Exception e) {
      throw new StorageException(Result.Error, e.getMessage());
    }
  });

  public final Function<DatabaseParameters, Configuration> mapper;

  Database(final Function<DatabaseParameters, Configuration> mapper) {
    this.mapper = mapper;
  }

  public static Database from(final String name) {
    if (name == null) {
      return null;
    }

    final Database database = valueOf(name.trim().toUpperCase());

    if (database == null) {
      throw new IllegalArgumentException("The informed database is not supported");
    }

    return database;
  }
}
