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
import io.vlingo.xoom.turbo.annotation.persistence.Persistence.StorageType;

import java.util.List;

public class ObjectStoreActorBuilder implements StoreActorBuilder {

  @Override
  @SuppressWarnings("rawtypes")
  public <T> T build(final Stage stage, final List<Dispatcher> dispatchers, final Configuration configuration) {
    //TODO: Implement Object Store Actor Builder
    throw new UnsupportedOperationException("Object Store is not supported");
  }

  @Override
  public boolean support(final StorageType storageType, final DatabaseType databaseType) {
    return storageType.isObjectStore();
  }

}
