// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.storage;

import io.vlingo.xoom.turbo.actors.Settings;
import org.junit.Assert;
import org.junit.Test;

public class DatabaseParametersTest {

    @Test
    public void testDomainParametersLoad() {
        final DatabaseParameters parameters = new DatabaseParameters(Model.DOMAIN, Settings.properties());
        Assert.assertEquals("IN_MEMORY", parameters.database);
        Assert.assertTrue(parameters.autoCreate);
    }

    @Test
    public void testCommandParametersLoad() {
        final DatabaseParameters parameters = new DatabaseParameters(Model.COMMAND, Settings.properties());
        Assert.assertEquals("IN_MEMORY", parameters.database);
        Assert.assertTrue(parameters.autoCreate);
    }

    @Test
    public void testQueryParametersLoad() {
        final DatabaseParameters parameters = new DatabaseParameters(Model.QUERY, Settings.properties(), true);
        Assert.assertEquals("MYSQL", parameters.database);
        Assert.assertEquals("STORAGE_TEST", parameters.name);
        Assert.assertEquals("jdbc:mysql://localhost:2215/", parameters.url);
        Assert.assertEquals("com.mysql.cj.jdbc.Driver", parameters.driver);
        Assert.assertEquals("xoom_test", parameters.username);
        Assert.assertEquals("vlingo123", parameters.password);
        Assert.assertEquals("MAIN", parameters.originator);
        Assert.assertEquals("5", parameters.attempts);
        Assert.assertTrue(parameters.autoCreate);
    }

}
