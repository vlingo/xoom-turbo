// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.vlingo.actors.World;

public class XoomTest {

    private static final String BootWorldName = "test-boot";

    @Test
    public void testThatWorldBoots() {
        Xoom.main(new String[] { BootWorldName });

        final World world = Xoom.xoom().world();

        assertEquals(BootWorldName, world.name());
    }

    @Test
    public void testThatWorldStarts() {
        final World world = Xoom.start(BootWorldName).world();

        assertEquals(BootWorldName, world.name());
    }

}
