// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.storage;

import io.vlingo.actors.Stage;
import io.vlingo.symbio.store.common.jdbc.Configuration;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.xoom.annotation.persistence.Persistence.StorageType;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;

public interface StoreActorBuilder {

    List<StoreActorBuilder> BUILDERS =
            Arrays.asList(new InMemoryStateStoreActorBuilder(), new DefaultStateStoreActorBuilder(),
                    new InMemoryJournalActorBuilder(), new DefaultJournalActorBuilder(),
                    new ObjectStoreActorBuilder());

    static <T> T from(final Stage stage,
                      final Model model,
                      final Dispatcher dispatcher,
                      final StorageType storageType,
                      final Properties properties,
                      final boolean autoDatabaseCreation) {
        return from(stage, model, Arrays.asList(dispatcher), storageType, properties, autoDatabaseCreation);
    }

    static <T> T from(final Stage stage,
                      final Model model,
                      final List<Dispatcher> dispatcher,
                      final StorageType storageType,
                      final Properties properties,
                      final boolean autoDatabaseCreation) {
        final Configuration configuration =
                new DatabaseParameters(model, properties, autoDatabaseCreation)
                        .mapToConfiguration();

        final DatabaseType databaseType =
                DatabaseType.retrieveFromConfiguration(configuration);

        final Predicate<StoreActorBuilder> filter =
                resolver -> resolver.support(storageType, databaseType);

        return BUILDERS.stream().filter(filter).findFirst().get()
                .build(stage, dispatcher, configuration);
    }

    <T> T build(final Stage stage, final List<Dispatcher> dispatchers, final Configuration configuration);

    boolean support(final StorageType storageType, final DatabaseType databaseType);

}
