// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.exchange;

import io.vlingo.lattice.exchange.ConnectionSettings;
import io.vlingo.xoom.actors.Settings;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ExchangeParametersTest {

    @Test
    public void testThatConnectionSettingsAreMapped() {
        final ConnectionSettings firstSettings =
                ExchangeParameters.of("first").mapToConnectionSettings();

        Assert.assertEquals("first-exchange", firstSettings.hostName);
        Assert.assertEquals("vlingo01", firstSettings.username);
        Assert.assertEquals("vlingo-pass01", firstSettings.password);
        Assert.assertEquals("virtual-first-exchange", firstSettings.virtualHost);
        Assert.assertEquals(1000, firstSettings.port);

        final ConnectionSettings secondSettings =
                ExchangeParameters.of("second").mapToConnectionSettings();

        Assert.assertEquals("second-exchange", secondSettings.hostName);
        Assert.assertEquals("vlingo02", secondSettings.username);
        Assert.assertEquals("vlingo-pass02", secondSettings.password);
        Assert.assertEquals("virtual-second-exchange", secondSettings.virtualHost);
        Assert.assertEquals(1001, secondSettings.port);

        Assert.assertEquals(2, ExchangeParameters.all().size());
    }

    @Before
    public void loadParameters() {
        ExchangeParameters.load(Settings.properties());
    }


}
