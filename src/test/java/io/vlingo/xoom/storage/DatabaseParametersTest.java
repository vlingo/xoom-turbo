// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.storage;

import io.vlingo.xoom.actors.Settings;
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
        Assert.assertEquals("HSQLDB", parameters.database);
        Assert.assertEquals("DB_CONFIG_TEST", parameters.name);
        Assert.assertEquals("jdbc:hsqldb:mem:", parameters.url);
        Assert.assertEquals("org.hsqldb.jdbcDriver", parameters.driver);
        Assert.assertEquals("sa", parameters.username);
        Assert.assertEquals("pwd", parameters.password);
        Assert.assertEquals("MAIN", parameters.originator);
        Assert.assertTrue(parameters.autoCreate);
    }

}
