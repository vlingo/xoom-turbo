// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.storage;

import io.vlingo.xoom.actors.Logger;
import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.symbio.store.common.jdbc.Configuration;
import io.vlingo.xoom.symbio.store.state.StateStore.StorageDelegate;
import io.vlingo.xoom.symbio.store.state.jdbc.hsqldb.HSQLDBStorageDelegate;
import io.vlingo.xoom.symbio.store.state.jdbc.mysql.MySQLStorageDelegate;
import io.vlingo.xoom.symbio.store.state.jdbc.postgres.PostgresStorageDelegate;
import io.vlingo.xoom.symbio.store.state.jdbc.yugabyte.YugaByteStorageDelegate;
import io.vlingo.xoom.turbo.annotation.persistence.Persistence.StorageType;

import java.util.function.BiFunction;
import java.util.stream.Stream;

public enum DatabaseType {

  IN_MEMORY("InMemory"),
  POSTGRES("Postgres", (configuration, logger) -> new PostgresStorageDelegate(configuration, logger)),
  HSQLDB("HSQLDB", (configuration, logger) -> new HSQLDBStorageDelegate(configuration, logger)),
  MYSQL("MySQL", (configuration, logger) -> new MySQLStorageDelegate(configuration, logger)),
  YUGA_BYTE("YugaByte", (configuration, logger) -> new YugaByteStorageDelegate(configuration, logger));

  private final String name;
  private final BiFunction<Configuration, Logger, StorageDelegate> stateStoreDelegateInstantiator;

  DatabaseType(final String name) {
    this(name, null);
  }

  DatabaseType(final String name, final BiFunction<Configuration, Logger, StorageDelegate> instantiator) {
    this.name = name;
    this.stateStoreDelegateInstantiator = instantiator;
  }

  public static DatabaseType retrieveFromConfiguration(final Configuration configuration) {
    if (configuration == null) {
      return IN_MEMORY;
    }

    final String name =
            configuration.databaseType.name();

    return Stream.of(values()).filter(type -> type.hasName(name))
            .findFirst().orElseThrow(() -> new IllegalArgumentException(name + " is not supported"));
  }

  public StorageDelegate buildStorageDelegate(final Stage stage,
                                              final StorageType storageType,
                                              final Configuration configuration) {
    if (storageType.isStateStore()) {
      if (stateStoreDelegateInstantiator != null) {
        return stateStoreDelegateInstantiator.apply(configuration, stage.world().defaultLogger());
      }
    }
    throw new UnsupportedOperationException(this + " does not support StorageDelegate for " + storageType);
  }

  public boolean isInMemory() {
    return equals(IN_MEMORY);
  }

  public boolean hasName(final String name) {
    return this.name.equals(name);
  }
}