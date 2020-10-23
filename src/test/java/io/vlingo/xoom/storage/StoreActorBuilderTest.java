// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.storage;

import io.vlingo.actors.World;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.state.StateStore;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static io.vlingo.xoom.annotation.persistence.Persistence.StorageType.JOURNAL;
import static io.vlingo.xoom.annotation.persistence.Persistence.StorageType.STATE_STORE;

public class StoreActorBuilderTest {

    protected World world;

    @Test
    public void testThatMySqlJournalActorIsBuilt() {
        final Journal journal =
                StoreActorBuilder.from(world.stage(), Model.COMMAND,
                        new MockDispatcher<>(), JOURNAL, defaultDatabaseProperties(Model.COMMAND), false);

        Assert.assertNotNull(journal);
    }

    @Test
    public void testThatInMemoryJournalActorIsBuilt() {
        final Journal journal =
                StoreActorBuilder.from(world.stage(), Model.COMMAND,
                        new MockDispatcher<>(), JOURNAL, inMemoryDatabaseProperties(), false);

        Assert.assertNotNull(journal);
    }

    @Test
    public void testThatInMemoryStateStoreActorIsBuilt() {
        final StateStore stateStore =
                StoreActorBuilder.from(world.stage(), Model.COMMAND,
                        new MockDispatcher<>(), STATE_STORE, inMemoryDatabaseProperties(), false);

        Assert.assertNotNull(stateStore);
    }


    @Test
    public void testThatMySqlQueryStateStoreActorIsBuilt() {
        final StateStore stateStore =
                StoreActorBuilder.from(world.stage(), Model.QUERY,
                        new MockDispatcher<>(), STATE_STORE, defaultDatabaseProperties(Model.QUERY), false);

        Assert.assertNotNull(stateStore);
    }

    private Properties defaultDatabaseProperties(final Model model) {
        final String prefix = Model.QUERY.equals(model) ? "query." : "";
        final Properties properties = new Properties();
        properties.put(prefix + "database", "MYSQL");
        properties.put(prefix + "database.name", "STORAGE_TEST");
        properties.put(prefix + "database.url", "jdbc:mysql://localhost:2215/");
        properties.put(prefix + "database.driver", "com.mysql.cj.jdbc.Driver");
        properties.put(prefix + "database.username", "vlingo_test");
        properties.put(prefix + "database.password", "vlingo123");
        properties.put(prefix + "database.originator", "MAIN");
        return properties;
    }

    private Properties inMemoryDatabaseProperties() {
        final Properties properties = new Properties();
        properties.put("database", "IN_MEMORY");
        return properties;
    }

    @Before
    public void startWorld() {
        world = World.startWithDefaults("store-actor-build-tests");
    }

    @After
    public void terminateWorld() {
        world.terminate();
    }
}
