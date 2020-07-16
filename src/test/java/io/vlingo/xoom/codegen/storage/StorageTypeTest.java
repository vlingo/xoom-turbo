// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.storage;

import org.junit.Assert;
import org.junit.Test;

import static io.vlingo.xoom.codegen.storage.DatabaseType.*;
import static io.vlingo.xoom.codegen.storage.StorageType.*;

public class StorageTypeTest {

    @Test
    public void testStoreActorQualifiedClassNameRetrieval() {
        Assert.assertEquals("io.vlingo.symbio.store.journal.inmemory.InMemoryJournalActor",
                JOURNAL.actorFor(DatabaseType.IN_MEMORY));
        Assert.assertEquals("io.vlingo.symbio.store.object.inmemory.InMemoryObjectStoreActor",
                OBJECT_STORE.actorFor(DatabaseType.IN_MEMORY));
        Assert.assertEquals("io.vlingo.symbio.store.state.inmemory.InMemoryStateStoreActor",
                STATE_STORE.actorFor(DatabaseType.IN_MEMORY));

        Assert.assertEquals("io.vlingo.symbio.store.state.jdbc.JDBCStateStoreActor",
                STATE_STORE.actorFor(POSTGRES));
        Assert.assertEquals("io.vlingo.symbio.store.state.jdbc.JDBCStateStoreActor",
                STATE_STORE.actorFor(MYSQL));
        Assert.assertEquals("io.vlingo.symbio.store.state.jdbc.JDBCStateStoreActor",
                STATE_STORE.actorFor(YUGA_BYTE));
        Assert.assertEquals("io.vlingo.symbio.store.state.jdbc.JDBCStateStoreActor",
                STATE_STORE.actorFor(HSQLDB));
    }

}
