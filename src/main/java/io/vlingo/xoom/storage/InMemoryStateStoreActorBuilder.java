// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.storage;

import java.util.List;

import io.vlingo.actors.Stage;
import io.vlingo.symbio.store.common.jdbc.Configuration;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.symbio.store.state.inmemory.InMemoryStateStoreActor;
import io.vlingo.xoom.annotation.persistence.Persistence.StorageType;

public class InMemoryStateStoreActorBuilder implements StoreActorBuilder {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T> T build(final Stage stage, final List<Dispatcher> dispatchers, final Configuration configuration) {
        return (T) stage.actorFor(StateStore.class, InMemoryStateStoreActor.class, dispatchers);
    }

    @Override
    public boolean support(final StorageType storageType, final DatabaseType databaseType) {
        return storageType.isStateStore() && databaseType.isInMemory();
    }

}
