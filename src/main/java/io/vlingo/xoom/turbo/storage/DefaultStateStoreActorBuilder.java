// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.storage;

import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.State;
import io.vlingo.xoom.symbio.store.common.jdbc.Configuration;
import io.vlingo.xoom.symbio.store.dispatch.Dispatchable;
import io.vlingo.xoom.symbio.store.dispatch.Dispatcher;
import io.vlingo.xoom.symbio.store.dispatch.DispatcherControl;
import io.vlingo.xoom.symbio.store.dispatch.DispatcherControl.DispatcherControlDelegate;
import io.vlingo.xoom.symbio.store.dispatch.control.DispatcherControlActor;
import io.vlingo.xoom.symbio.store.state.StateStore;
import io.vlingo.xoom.symbio.store.state.StateStore.StorageDelegate;
import io.vlingo.xoom.symbio.store.state.jdbc.JDBCEntriesInstantWriter;
import io.vlingo.xoom.symbio.store.state.jdbc.JDBCEntriesWriter;
import io.vlingo.xoom.symbio.store.state.jdbc.JDBCStateStoreActor;
import io.vlingo.xoom.symbio.store.state.jdbc.JDBCStorageDelegate;
import io.vlingo.xoom.turbo.annotation.persistence.Persistence.StorageType;

import java.util.List;

import static io.vlingo.xoom.symbio.store.state.StateStore.DefaultCheckConfirmationExpirationInterval;
import static io.vlingo.xoom.symbio.store.state.StateStore.DefaultConfirmationExpiration;
import static io.vlingo.xoom.turbo.annotation.persistence.Persistence.StorageType.STATE_STORE;

public class DefaultStateStoreActorBuilder implements StoreActorBuilder {

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T> T build(final Stage stage,
                        final List<Dispatcher> dispatchers,
                        final Configuration configuration) {
        final StorageDelegate delegate =
                DatabaseType.retrieveFromConfiguration(configuration)
                        .buildStorageDelegate(stage, STATE_STORE, configuration);

        final Stage local = stage.world().stage();

        final DispatcherControl dispatcherControl =
                local.actorFor(DispatcherControl.class,
                        Definition.has(DispatcherControlActor.class,
                        new DispatcherControl.DispatcherControlInstantiator(dispatchers,
                                (DispatcherControlDelegate) delegate, DefaultCheckConfirmationExpirationInterval,
                                DefaultConfirmationExpiration)));

        final JDBCEntriesWriter entriesWriter =
                new JDBCEntriesInstantWriter((JDBCStorageDelegate) delegate,
                       typed(dispatchers), dispatcherControl);

        return (T) local.actorFor(StateStore.class, JDBCStateStoreActor.class, delegate, entriesWriter);
    }

    @SuppressWarnings("unchecked")
    private List<Dispatcher<Dispatchable<? extends Entry<?>,? extends State<?>>>> typed(List<?> dispatchers) {
        return (List<Dispatcher<Dispatchable<? extends Entry<?>, ? extends State<?>>>>) dispatchers;
    }

    @Override
    public boolean support(final StorageType storageType, final DatabaseType databaseType) {
        return storageType.isStateStore() && !databaseType.isInMemory();
    }

}
