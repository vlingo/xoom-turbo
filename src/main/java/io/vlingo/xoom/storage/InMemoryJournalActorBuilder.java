// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.storage;

import io.vlingo.actors.Definition;
import io.vlingo.actors.Stage;
import io.vlingo.symbio.store.common.jdbc.Configuration;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.xoom.annotation.persistence.Persistence.StorageType;

public class InMemoryJournalActorBuilder implements StoreActorBuilder {

    @Override
    public Object build(final Stage stage,
                        final Dispatcher dispatcher,
                        final Configuration configuration) {
        return Definition.parameters(dispatcher);
    }

    @Override
    public boolean support(final StorageType storageType, final DatabaseType databaseType) {
        return storageType.isJournal() && databaseType.isInMemory();
    }

}
