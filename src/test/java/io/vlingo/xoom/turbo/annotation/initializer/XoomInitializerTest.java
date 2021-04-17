// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.annotation.initializer;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;

import org.junit.After;
import org.junit.Test;

public class XoomInitializerTest {

    @Test
    public void testInitialization() throws Exception {
        XoomInitializer.main(new String[]{});

        try {
            new URL("http://127.0.0.1:18080").openConnection().connect();
        } catch (IOException e) {
            fail("Server did not open port 18080:" + e.getMessage());
        }
    }

    @After
    public void tearDown() throws Exception {
        XoomInitializer.instance().stopServer();
    }
}
