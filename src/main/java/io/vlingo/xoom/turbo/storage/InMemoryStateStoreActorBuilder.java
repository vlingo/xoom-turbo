// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.storage;

import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.symbio.store.common.jdbc.Configuration;
import io.vlingo.xoom.symbio.store.dispatch.Dispatcher;
import io.vlingo.xoom.symbio.store.state.StateStore;
import io.vlingo.xoom.symbio.store.state.inmemory.InMemoryStateStoreActor;
import io.vlingo.xoom.turbo.annotation.persistence.Persistence.StorageType;

import java.util.List;

public class InMemoryStateStoreActorBuilder implements StoreActorBuilder {

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public <T> T build(final Stage stage, final List<Dispatcher> dispatchers, final Configuration configuration) {
    final Stage local = stage.world().stage();

    return (T) local.actorFor(StateStore.class, InMemoryStateStoreActor.class, dispatchers, 5000L, 5000L);
  }

  @Override
  public boolean support(final StorageType storageType, final DatabaseType databaseType) {
    return storageType.isStateStore() && databaseType.isInMemory();
  }

}
