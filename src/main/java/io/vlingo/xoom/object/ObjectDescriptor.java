// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.object;

import io.vlingo.actors.Actor;
import io.vlingo.actors.Grid;
import io.vlingo.actors.Stage;
import io.vlingo.actors.World;

/**
 * A descriptor used to create runtime objects.
 * @param <P> the {@code Class<P>} of the protocol
 * @param <T> the {@code Class<T>} of the concrete actor type
 */
public class ObjectDescriptor<P, T extends Actor> {
  private final Class<P> protocol;
  private final Class<T> type;

  /**
   * Answer a new {@code ObjectDescriptor}.
   * @param <P> the {@code Class<P>} of the protocol
   * @param <T> the {@code Class<T>} of the concrete actor type
   * @param protocol the {@code Class<P>} protocol that the the object supports
   * @param type the {@code Class<T>} concrete Actor that implements the protocol
   * @return {@code ObjectDescriptor<P, T>}
   */
  public static <P, T extends Actor> ObjectDescriptor<P, T> descriptorFor(final Class<P> protocol, final Class<T> type) {
    return new ObjectDescriptor<P, T>(protocol, type);
  }

  /**
   * Constructs my state.
   * @param protocol the {@code Class<P>} protocol that the the object supports
   * @param type the {@code Class<T>} concrete Actor that implements the protocol
   */
  public ObjectDescriptor(final Class<P> protocol, final Class<T> type) {
    this.protocol = protocol;
    this.type = type;
  }

  /**
   * Answer a new instance of the {@code P} object within the {@code grid}. Note that
   * being in the {@code grid} the new object could be partitioned onto any node in
   * the {@code grid} cluster.
   * @param grid the Grid within which the object is instantiated
   * @return P
   */
  public P objectInstance(final Grid grid) {
    return grid.actorFor(protocol, type);
  }

  public P objectInstance(final Stage stage) {
    return stage.actorFor(protocol, type);
  }

  public P objectInstance(final World world) {
    return objectInstance(world.stage());
  }

  public Class<P> protocol() {
    return protocol;
  }

  public Class<T> type() {
    return type;
  }
}
