// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.scooter.model.object;

import io.vlingo.xoom.symbio.Metadata;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.store.object.StateObject;
import io.vlingo.xoom.turbo.scooter.model.Applied;
import io.vlingo.xoom.turbo.scooter.model.Entity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class ObjectEntity<S extends StateObject, C> extends Entity<S, C> {
  /**
   * Answer my unique identity, which much be provided by
   * my concrete extender by overriding.
   *
   * @return String
   */
  @Override
  public abstract String id();

  /**
   * Construct my default state.
   */
  protected ObjectEntity() {
  }

  /**
   * Apply {@code state} and {@code sources} to myself.
   *
   * @param state    the Object state to preserve
   * @param sources  the {@code List<Source<C>>} instances to apply
   * @param metadata the Metadata to apply along with source
   */
  protected void apply(final S state, final List<Source<C>> sources, final Metadata metadata) {
    apply(new Applied<>(state, sources, metadata));
  }

  /**
   * Apply {@code state}, {@code sources}, with {@code metadata} to myself.
   *
   * @param state    the Object state to preserve
   * @param source   the {@code Source<C>} to apply
   * @param metadata the Metadata to apply along with source
   */
  protected void apply(final S state, final Source<C> source, final Metadata metadata) {
    apply(new Applied<>(state, Arrays.asList(source), metadata));
  }

  /**
   * Apply {@code state} to myself.
   *
   * @param state the Object state to preserve
   */
  protected void apply(final S state) {
    apply(new Applied<>(state, Collections.emptyList(), metadata()));
  }

  /**
   * Apply {@code state} and {@code sources} to myself.
   *
   * @param state   the S state to preserve
   * @param sources the {@code List<Source<C>>} to apply
   */
  protected void apply(final S state, final List<Source<C>> sources) {
    apply(new Applied<>(state, sources, metadata()));
  }

  /**
   * Apply {@code state} and {@code source} to myself.
   *
   * @param state  the T typed state to apply
   * @param source the {@code Source<C>} instances to apply
   */
  protected void apply(final S state, final Source<C> source) {
    apply(new Applied<>(state, Arrays.asList(source), metadata()));
  }

  /**
   * Answer a {@code List<Source<C>>} from the varargs {@code sources}.
   *
   * @param sources the varargs {@code Source<C>} of sources to answer as a {@code List<Source<C>>}
   * @return {@code List<Source<C>>}
   */
  @SafeVarargs
  protected final List<Source<C>> asList(final Source<C>... sources) {
    return Arrays.asList(sources);
  }

  /**
   * Answer a representation of a number of segments as a
   * composite id. The implementor of {@code id()} would use
   * this method if the its id is built from segments.
   *
   * @param separator  the String separator the insert between segments
   * @param idSegments the varargs String of one or more segments
   * @return String
   */
  protected String idFrom(final String separator, final String... idSegments) {
    final StringBuilder builder = new StringBuilder();
    builder.append(idSegments[0]);
    for (int idx = 1; idx < idSegments.length; ++idx) {
      builder.append(separator).append(idSegments[idx]);
    }
    return builder.toString();
  }

  /**
   * Answer my {@code Metadata}. Must override if {@code Metadata} is to be supported.
   *
   * @return Metadata
   */
  protected Metadata metadata() {
    return Metadata.nullMetadata();
  }

  /**
   * Received by my extender when my state object has been preserved and restored.
   * Must be overridden by my extender.
   *
   * @param stateObject the T typed state object
   */
  protected abstract void stateObject(final S stateObject);

  /**
   * Apply by setting {@code applied()} and setting state.
   *
   * @param applied the {@code Applied<S,C>} to apply
   */
  private void apply(final Applied<S, C> applied) {
    this.applied(applied);
    this.stateObject(applied.state);
  }
}
