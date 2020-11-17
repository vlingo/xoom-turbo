// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom;

import io.vlingo.xoom.actors.Settings;
import org.junit.Assert;
import org.junit.Test;

public class SettingsTest {

    @Test
    public void testThatSettingsAreLoadedForBlockingMailbox() {
        Assert.assertEquals(14, Settings.properties().size());
    }

}
