// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.scooter.model;

/**
 * Base of all entities.
 * @param <S> the State type
 * @param <C> the Source type
 */
public abstract class Entity<S,C> {
  private Applied<S,C> applied;

  /**
   * Answer my {@code applied}.
   * @return {@code Applied<S,C>}
   */
  public Applied<S,C> applied() {
    return applied;
  }

  /**
   * Answer my {@code currentVersion}, which, if zero, indicates that the
   * receiver is being initially constructed or reconstituted.
   * @return int
   */
  public int currentVersion() {
    return 0;
  }

  /**
   * Answer my {@code nextVersion}, which is {@code currentVersion() + 1}.
   * @return int
   */
  public int nextVersion() {
    return currentVersion() + 1;
  }

  /**
   * Answer my unique identity, which much be provided by
   * my concrete extender by overriding.
   * @return String
   */
  public abstract String id();

  protected Entity() { }

  protected void applied(final Applied<S,C> applied) {
    if (this.applied == null) {
      this.applied = applied;
    } else if (applied.state != null) {
      if (applied.metadata.isEmpty()) {
        this.applied = this.applied.alongWith(applied.state, applied.sources(), this.applied.metadata);
      } else {
        this.applied = this.applied.alongWith(applied.state, applied.sources(), applied.metadata);
      }
    } else if (applied.state == null) {
      if (applied.metadata.isEmpty()) {
        this.applied = this.applied.alongWith(applied.state, applied.sources(), this.applied.metadata);
      } else {
        this.applied = this.applied.alongWith(applied.state, applied.sources(), applied.metadata);
      }
    }
  }
}
