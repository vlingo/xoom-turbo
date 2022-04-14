// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
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
import io.vlingo.xoom.symbio.store.Result;
import io.vlingo.xoom.symbio.store.StorageException;
import io.vlingo.xoom.symbio.store.common.jdbc.Configuration;
import io.vlingo.xoom.symbio.store.dispatch.Dispatchable;
import io.vlingo.xoom.symbio.store.dispatch.Dispatcher;
import io.vlingo.xoom.symbio.store.dispatch.DispatcherControl;
import io.vlingo.xoom.symbio.store.dispatch.control.DispatcherControlActor;
import io.vlingo.xoom.symbio.store.journal.Journal;
import io.vlingo.xoom.symbio.store.journal.jdbc.JDBCDispatcherControlDelegate;
import io.vlingo.xoom.symbio.store.journal.jdbc.JDBCJournalActor;
import io.vlingo.xoom.symbio.store.journal.jdbc.JDBCJournalInstantWriter;
import io.vlingo.xoom.symbio.store.journal.jdbc.JDBCJournalWriter;
import io.vlingo.xoom.turbo.annotation.persistence.Persistence.StorageType;

import java.util.List;

public class DefaultJournalActorBuilder implements StoreActorBuilder {

  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public <T> T build(final Stage stage,
                     final List<Dispatcher> dispatchers,
                     final Configuration configuration) {
    try {
      final JDBCDispatcherControlDelegate dispatcherControlDelegate =
              new JDBCDispatcherControlDelegate(Configuration.cloneOf(configuration), stage.world().defaultLogger());

      final Stage local = stage.world().stage();

      final DispatcherControl dispatcherControl = local.actorFor(DispatcherControl.class,
              Definition.has(DispatcherControlActor.class,
                      new DispatcherControl.DispatcherControlInstantiator(
                              dispatchers,
                              dispatcherControlDelegate,
                              Journal.DefaultCheckConfirmationExpirationInterval,
                              Journal.DefaultConfirmationExpiration)));

      final JDBCJournalWriter journalWriter =
              new JDBCJournalInstantWriter(configuration, typed(dispatchers), dispatcherControl);

      return (T) local.world().actorFor(Journal.class, JDBCJournalActor.class, configuration, journalWriter);
    } catch (final Exception e) {
      throw new StorageException(Result.Error, e.getMessage());
    }
  }

  @SuppressWarnings("unchecked")
  private List<Dispatcher<Dispatchable<Entry<String>, State.TextState>>> typed(final List<?> dispatchers) {
    return (List<Dispatcher<Dispatchable<Entry<String>, State.TextState>>>) dispatchers;
  }

  @Override
  public boolean support(final StorageType storageType, final DatabaseType databaseType) {
    return storageType.isJournal() && !databaseType.isInMemory();
  }
}
