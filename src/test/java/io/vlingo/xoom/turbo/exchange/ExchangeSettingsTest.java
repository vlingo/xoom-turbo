// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.exchange;

import io.vlingo.xoom.lattice.exchange.ConnectionSettings;
import io.vlingo.xoom.turbo.actors.Settings;
import org.junit.Assert;
import org.junit.Test;

public class ExchangeSettingsTest {

  @Test
  public void testThatSettingsIsLoaded() {
    Assert.assertEquals("first", ExchangeSettings.loadOne(Settings.properties()).exchangeName);
  }

  @Test
  public void testThatConnectionSettingsAreMapped() {
    ExchangeSettings.load(Settings.properties());

    final ConnectionSettings firstSettings =
            ExchangeSettings.of("first").mapToConnection();

    Assert.assertEquals("first-exchange", firstSettings.hostName);
    Assert.assertEquals("vlingo01", firstSettings.username);
    Assert.assertEquals("xoom-pass01", firstSettings.password);
    Assert.assertEquals("virtual-first-exchange", firstSettings.virtualHost);
    Assert.assertEquals(1000, firstSettings.port);

    final ConnectionSettings secondSettings =
            ExchangeSettings.of("second").mapToConnection();

    Assert.assertEquals("second-exchange", secondSettings.hostName);
    Assert.assertEquals("vlingo02", secondSettings.username);
    Assert.assertEquals("xoom-pass02", secondSettings.password);
    Assert.assertEquals("virtual-second-exchange", secondSettings.virtualHost);
    Assert.assertEquals(1001, secondSettings.port);

    Assert.assertEquals(2, ExchangeSettings.all().size());
  }

}
