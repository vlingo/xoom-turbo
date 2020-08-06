// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.storage;

import io.vlingo.symbio.store.StorageException;
import org.junit.Assert;
import org.junit.Test;

import static io.vlingo.xoom.storage.Model.DOMAIN;
import static io.vlingo.xoom.storage.Model.QUERY;

public class DatabaseConfigurationTest {

    @Test
    public void testInMemoryDatabaseConfigurationLoad() {
        Assert.assertNull(DatabaseConfiguration.load(DOMAIN));
    }

    @Test(expected = StorageException.class)
    public void testUnreachableDatabaseConfigurationLoad() {
        DatabaseConfiguration.load(QUERY);
    }

}
