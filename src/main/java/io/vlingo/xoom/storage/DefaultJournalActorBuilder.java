// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.storage;

import io.vlingo.actors.Definition;
import io.vlingo.actors.Stage;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;
import io.vlingo.symbio.store.common.jdbc.Configuration;
import io.vlingo.symbio.store.dispatch.Dispatchable;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.dispatch.DispatcherControl;
import io.vlingo.symbio.store.dispatch.control.DispatcherControlActor;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.journal.jdbc.JDBCDispatcherControlDelegate;
import io.vlingo.symbio.store.journal.jdbc.JDBCJournalActor;
import io.vlingo.symbio.store.journal.jdbc.JDBCJournalInstantWriter;
import io.vlingo.symbio.store.journal.jdbc.JDBCJournalWriter;
import io.vlingo.xoom.annotation.persistence.Persistence.StorageType;

import java.util.List;

public class DefaultJournalActorBuilder implements StoreActorBuilder {

    @Override
    public <T> T build(final Stage stage,
                       final List<Dispatcher> dispatchers,
                       final Configuration configuration) {
        try {
            final JDBCDispatcherControlDelegate dispatcherControlDelegate =
                    new JDBCDispatcherControlDelegate(Configuration.cloneOf(configuration), stage.world().defaultLogger());

            final DispatcherControl dispatcherControl = stage.actorFor(DispatcherControl.class,
                    Definition.has(DispatcherControlActor.class,
                            new DispatcherControl.DispatcherControlInstantiator(
                                    dispatchers,
                                    dispatcherControlDelegate,
                                    Journal.DefaultCheckConfirmationExpirationInterval,
                                    Journal.DefaultConfirmationExpiration)));

            final JDBCJournalWriter journalWriter =
                    new JDBCJournalInstantWriter(configuration, typed(dispatchers), dispatcherControl);

            return (T) stage.world().actorFor(Journal.class, JDBCJournalActor.class, configuration, journalWriter);
        } catch (final Exception e) {
            throw new StorageException(Result.Error, e.getMessage());
        }
    }

    private List<Dispatcher<Dispatchable<Entry<String>, State.TextState>>> typed(final List<?> dispatchers) {
        return (List<Dispatcher<Dispatchable<Entry<String>, State.TextState>>>) dispatchers;
    }

    @Override
    public boolean support(final StorageType storageType, final DatabaseType databaseType) {
        return storageType.isJournal() && !databaseType.isInMemory();
    }
}
