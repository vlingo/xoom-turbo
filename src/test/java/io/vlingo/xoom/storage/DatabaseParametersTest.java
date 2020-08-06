// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.storage;

import org.junit.Assert;
import org.junit.Test;

public class DatabaseParametersTest {

    @Test
    public void testDomainParametersLoad() {
        final DatabaseParameters parameters = new DatabaseParameters(Model.DOMAIN);
        Assert.assertEquals("IN_MEMORY", parameters.database);
        Assert.assertTrue(parameters.autoCreate);
    }

    @Test
    public void testCommandParametersLoad() {
        final DatabaseParameters parameters = new DatabaseParameters(Model.COMMAND);
        Assert.assertEquals("IN_MEMORY", parameters.database);
        Assert.assertTrue(parameters.autoCreate);
    }

    @Test
    public void testQueryParametersLoad() {
        final DatabaseParameters parameters = new DatabaseParameters(Model.QUERY, false);
        Assert.assertEquals("POSTGRES", parameters.database);
        Assert.assertEquals("DB_CONFIG_TEST", parameters.name);
        Assert.assertEquals("jdbc:postgresql://localhost/", parameters.url);
        Assert.assertEquals("org.postgresql.Driver", parameters.driver);
        Assert.assertEquals("vlingo_test", parameters.username);
        Assert.assertEquals("vlingo123", parameters.password);
        Assert.assertEquals("MAIN", parameters.originator);
        Assert.assertFalse(parameters.autoCreate);
    }

}
