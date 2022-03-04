// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.storage;

import io.vlingo.xoom.turbo.ComponentRegistry;
import io.vlingo.xoom.turbo.EnvironmentVariables.EnvironmentVariablesRetriever;
import io.vlingo.xoom.turbo.MockEnvironmentVariables;
import io.vlingo.xoom.turbo.actors.Settings;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Properties;

public class DatabaseParametersTest {

    @Test
    public void testThatDomainParametersAreLoaded() {
        final DatabaseParameters parameters = new DatabaseParameters(Model.DOMAIN, Settings.properties());
        Assert.assertEquals("IN_MEMORY", parameters.database);
        Assert.assertTrue(parameters.autoCreate);
    }

    @Test
    public void testThatCommandParametersAreLoaded() {
        final DatabaseParameters parameters = new DatabaseParameters(Model.COMMAND, Settings.properties());
        Assert.assertEquals("IN_MEMORY", parameters.database);
        Assert.assertTrue(parameters.autoCreate);
    }

    @Test
    public void testThatQueryParametersAreLoaded() {
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

    @Test
    public void testQueryParametersAreLoadedEnvVars() {
        final DatabaseParameters parameters = new DatabaseParameters(Model.QUERY, new Properties(), true);
        Assert.assertEquals("MYSQL", parameters.database);
        Assert.assertEquals("12F", parameters.name);
        Assert.assertEquals("jdbc:mysql://localhost:9001/", parameters.url);
        Assert.assertEquals("com.mysql.cj.jdbc.Driver", parameters.driver);
        Assert.assertEquals("12FDB", parameters.username);
        Assert.assertEquals("vlingo12F", parameters.password);
        Assert.assertEquals("FTI", parameters.originator);
        Assert.assertEquals("2", parameters.attempts);
        Assert.assertTrue(parameters.autoCreate);
    }

    @Before
    public void setUp() {
        ComponentRegistry.register(EnvironmentVariablesRetriever.class, mockEnvironmentVariables);
    }

    @After
    public void clear() {
        ComponentRegistry.clear();
    }

    private final MockEnvironmentVariables mockEnvironmentVariables = new MockEnvironmentVariables(new HashMap<String, String>(){
      private static final long serialVersionUID = 1L;

      {
        put("VLINGO_XOOM_QUERY_DATABASE", "MYSQL");
        put("VLINGO_XOOM_QUERY_DATABASE_NAME", "12F");
        put("VLINGO_XOOM_QUERY_DATABASE_URL", "jdbc:mysql://localhost:9001/");
        put("VLINGO_XOOM_QUERY_DATABASE_DRIVER", "com.mysql.cj.jdbc.Driver");
        put("VLINGO_XOOM_QUERY_DATABASE_USERNAME", "12FDB");
        put("VLINGO_XOOM_QUERY_DATABASE_PASSWORD", "vlingo12F");
        put("VLINGO_XOOM_QUERY_DATABASE_ORIGINATOR", "FTI");
        put("VLINGO_XOOM_QUERY_DATABASE_CONNECTION_ATTEMPTS", "2");
    }});
}
