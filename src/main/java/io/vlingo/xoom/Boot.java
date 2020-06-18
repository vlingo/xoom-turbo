// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom;

import io.vlingo.actors.World;

public class Boot {

    private static World zoomBootWorld;

    public static void main(final String[] args) {
        final String name = args.length > 0 ? args[0] : "vlingo-xoom";

        zoomBootWorld = start(name);
    }

    public static World zoomBootWorld() {
        return zoomBootWorld;
    }

    /**
     * Answers a new {@code World} with the given {@code name} and that is configured with
     * the contents of the {@code vlingo-zoom.properties} file.
     * @param name the {@code String} name to assign to the new {@code World} instance
     * @return {@code World}
     */
    public static World start(final String name) {
        zoomBootWorld = World.start(name, io.vlingo.xoom.actors.Properties.properties);

        return zoomBootWorld;
    }

}
