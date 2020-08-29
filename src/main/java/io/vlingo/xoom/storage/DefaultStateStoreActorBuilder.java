// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.storage;

import io.vlingo.actors.ActorInstantiator;
import io.vlingo.actors.Definition;
import io.vlingo.actors.Stage;
import io.vlingo.symbio.store.common.jdbc.Configuration;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.dispatch.DispatcherControl;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.symbio.store.state.StateStore.StorageDelegate;
import io.vlingo.symbio.store.state.jdbc.JDBCStateStoreActor;
import io.vlingo.xoom.annotation.persistence.Persistence.StorageType;

import static io.vlingo.xoom.annotation.persistence.Persistence.StorageType.STATE_STORE;

public class DefaultStateStoreActorBuilder implements StoreActorBuilder {

    @Override
    public Object build(final Stage stage,
                        final Dispatcher dispatcher,
                        final Configuration configuration) {
        final StorageDelegate storageDelegate =
                DatabaseType.retrieveFromConfiguration(configuration)
                        .buildStorageDelegate(stage, STATE_STORE, configuration);

        final ActorInstantiator<?> instantiator =
                new JDBCStateStoreActor.JDBCStateStoreInstantiator();

        instantiator.set("dispatcher", dispatcher);
        instantiator.set("delegate", storageDelegate);

        return stage.actorFor(new Class<?>[] { StateStore.class, DispatcherControl.class },
                Definition.has(JDBCStateStoreActor.class, instantiator));
    }

    @Override
    public boolean support(final StorageType storageType, final DatabaseType databaseType) {
        return storageType.isStateStore() && !databaseType.isInMemory();
    }

}
