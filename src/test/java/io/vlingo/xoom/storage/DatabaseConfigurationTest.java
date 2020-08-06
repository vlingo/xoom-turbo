// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.storage;

import io.vlingo.symbio.store.common.jdbc.Configuration;
import io.vlingo.symbio.store.common.jdbc.DatabaseType;
import org.junit.Assert;
import org.junit.Test;

import static io.vlingo.xoom.storage.Model.DOMAIN;
import static io.vlingo.xoom.storage.Model.QUERY;

public class DatabaseConfigurationTest {

    @Test
    public void testInMemoryDatabaseConfigurationLoad() {
        Assert.assertNull(DatabaseConfiguration.load(DOMAIN));
    }

    @Test
    public void testUnreachableDatabaseConfigurationLoad() {
        final Configuration configuration = DatabaseConfiguration.load(QUERY);
        Assert.assertEquals(DatabaseType.HSQLDB, configuration.databaseType);
        Assert.assertEquals("DB_CONFIG_TEST", configuration.actualDatabaseName);
        Assert.assertTrue(configuration.createTables);
    }

}
