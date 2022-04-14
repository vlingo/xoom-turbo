// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo;

import io.vlingo.xoom.actors.World;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BootTest {

    private static final String BootWorldName = "test-boot";

    @Test
    public void testThatWorldStarts() throws Exception {
        final World world = Boot.start(BootWorldName).world();
        assertEquals(BootWorldName, world.name());
    }

}
