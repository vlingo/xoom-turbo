// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.server;

import io.vlingo.http.resource.Configuration;

public class ServerConfigurationProvider {

    private static final Integer DEFAULT_PORT = 18080;

    public int resolvePort(final String worldName, final String [] args) {
        try {
            return Integer.parseInt(args[0]);
        } catch (final Exception e) {
            System.out.println(worldName  + ": Command line does not provide a valid port; defaulting to: " + DEFAULT_PORT);
            return DEFAULT_PORT;
        }
    }

    public Configuration.Sizing sizing() {
        return Configuration.Sizing.define();
    }

    public Configuration.Timing timing() {
        return Configuration.Timing.define();
    }

}
