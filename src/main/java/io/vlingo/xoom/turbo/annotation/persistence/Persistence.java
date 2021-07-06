// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.persistence;

import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.symbio.store.journal.Journal;
import io.vlingo.xoom.symbio.store.journal.NoOpJournalActor;
import io.vlingo.xoom.symbio.store.object.NoOpObjectStoreActor;
import io.vlingo.xoom.symbio.store.object.ObjectStore;
import io.vlingo.xoom.symbio.store.state.NoOpStateStoreActor;
import io.vlingo.xoom.symbio.store.state.StateStore;

public @interface Persistence {

  String basePackage();

  StorageType storageType();

  boolean cqrs() default false;

  enum StorageType {
    NONE, STATE_STORE, OBJECT_STORE, JOURNAL;

    public boolean isStateStore() {
      return equals(STATE_STORE);
    }

    public boolean isJournal() {
      return equals(JOURNAL);
    }

    public boolean isObjectStore() {
      return equals(OBJECT_STORE);
    }

    @SuppressWarnings("unchecked")
    public <T> T resolveNoOpStore(final Stage stage) {
      final Stage local = stage.world().stage();
      switch (this) {
        case STATE_STORE:
          return (T) local.actorFor(StateStore.class, NoOpStateStoreActor.class);
        case OBJECT_STORE:
          return (T) local.actorFor(ObjectStore.class, NoOpObjectStoreActor.class);
        case JOURNAL:
          return (T) local.actorFor(Journal.class, NoOpJournalActor.class);
        default:
          throw new IllegalStateException("Unable to resolve no operation store for " + this);
      }
    }
  }

}
