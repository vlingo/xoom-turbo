// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom;

import java.util.concurrent.atomic.AtomicReference;

import io.vlingo.actors.Grid;
import io.vlingo.actors.Logger;
import io.vlingo.actors.World;
import io.vlingo.xoom.object.ObjectDescriptor;

public class Xoom {
    private static final String DefaultWorldName = "vlingo-xoom";
    private static final String GridPostfix = "-grid";

    private static final AtomicReference<Xoom> xoom = new AtomicReference<>();

    private Grid grid;
    @SuppressWarnings("unused")
    private ShutdownHook shutdownHook;
    private World world;

    public static void main(final String[] args) {
        final String name = args.length > 0 ? args[0] : DefaultWorldName;

        start(name);
    }

    /**
     * Answer the {@code Xoom}.
     * @return Xoom
     */
    public static Xoom xoom() {
        return xoom.get();
    }

    /**
     * Answers a new {@code World} with the given {@code name} and that is configured with
     * the contents of the {@code vlingo-zoom.properties} file.
     * @param name the {@code String} name to assign to the new {@code World} instance
     * @return {@code World}
     */
    public static synchronized Xoom start(final String name) {
        if (xoom.get() == null) {
          xoom.set(new Xoom(name));
        }
        return xoom.get();
    }

    /**
     * Answer a new object of type {@code T} from the {@code ObjectDescriptor<T, ?>}
     * using my {@code grid}.
     * @param <T> the protocol of the object
     * @param descriptor the {@code ObjectDescriptor<T, ?>} through which the new object instance will be obtained
     * @return T
     */
    public <T> T objectInstance(final ObjectDescriptor<T, ?> descriptor) {
      return descriptor.objectInstance(grid);
    }

    /**
     * Answer my {@code Grid}.
     * @return Grid
     */
    public Grid grid() {
        return xoom.get().grid;
    }

    /**
     * Answer my {@code World}.
     * @return World
     */
    public World world() {
        return xoom.get().world;
    }

    /**
     * Construct my state with a {@code name}.
     * @param name the String name of my World
     */
    private Xoom(final String name) {
        try {
          this.world = World.start(name);
          this.grid = Grid.start(world, name + GridPostfix);
          this.shutdownHook = new ShutdownHook();
        } catch (Exception e) {
          System.out.println("Xoom failed to start because: " + e.getMessage());
          e.printStackTrace();
        }
    }

    private final class ShutdownHook {
      private ShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
          if (xoom.get() != null) {
            final World world = xoom.get().world;
            final Logger logger = world.defaultLogger();
            final String name = world.name();

            logger.info("\n==========");
            logger.info("Stopping Xoom: '" + name + "' ...");
            world.terminate();
            xoom.set(null);
            logger.info("Stopped Xoom: '" + name + "'");
          } else {
            System.out.println("Stopped unstarted Xoom."); // this should be unreacable
          }
        }));
      }
    }
}
