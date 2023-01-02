// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.scooter.model.stateful;

import io.vlingo.xoom.symbio.Metadata;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.turbo.scooter.model.Applied;
import io.vlingo.xoom.turbo.scooter.model.Entity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class StatefulEntity<S, C> extends Entity<S, C> {
  private int currentVersion;

  /**
   * Answer my currentVersion, which, if zero, indicates that the
   * receiver is being initially constructed or reconstituted.
   *
   * @return int
   */
  @Override
  public int currentVersion() {
    return currentVersion;
  }

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
  protected StatefulEntity() {
    this.currentVersion = 0;
  }

  /**
   * Apply my current {@code state} and {@code metadataValye} that was modified
   * due to the descriptive {@code operation}.
   *
   * @param state         the S typed state to apply
   * @param sources       the {@code List<Source>} instances to apply
   * @param metadataValue the String metadata value to apply along with the state
   * @param operation     the String descriptive name of the operation that caused the state modification
   */
  protected void apply(final S state, final List<Source<C>> sources, final String metadataValue, final String operation) {
    apply(new Applied<>(state, currentVersion + 1, sources, metadata(state, metadataValue, operation)));
  }

  /**
   * Apply my current {@code state} and {@code metadataValye} that was modified
   * due to the descriptive {@code operation}.
   *
   * @param state         the S typed state to apply
   * @param metadataValue the String metadata value to apply along with the state
   * @param operation     the String descriptive name of the operation that caused the state modification
   */
  protected void apply(final S state, final String metadataValue, final String operation) {
    apply(new Applied<>(state, currentVersion + 1, Collections.emptyList(), metadata(state, metadataValue, operation)));
  }

  /**
   * Apply my current {@code state} that was modified due to the descriptive {@code operation}.
   *
   * @param state     the S typed state to apply
   * @param sources   the {@code List<Source>} instances to apply
   * @param operation the String descriptive name of the operation that caused the state modification
   */
  protected void apply(final S state, final List<Source<C>> sources, final String operation) {
    apply(new Applied<>(state, currentVersion + 1, sources, metadata(state, null, operation)));
  }

  /**
   * Apply my current {@code state} that was modified due to the descriptive {@code operation}.
   *
   * @param state     the S typed state to apply
   * @param operation the String descriptive name of the operation that caused the state modification
   */
  protected void apply(final S state, final String operation) {
    apply(new Applied<>(state, currentVersion + 1, Collections.emptyList(), metadata(state, null, operation)));
  }

  /**
   * Apply my current {@code state} and {@code sources}.
   *
   * @param state   the S typed state to apply
   * @param sources the {@code List<Source<C>>} instances to apply
   */
  protected void apply(final S state, final List<Source<C>> sources) {
    apply(new Applied<>(state, currentVersion + 1, sources, metadata(state, null, null)));
  }

  /**
   * Apply my current {@code state} and {@code source}.
   *
   * @param state  the S typed state to apply
   * @param source the {@code Source<C>} instances to apply
   */
  protected void apply(final S state, final Source<C> source) {
    apply(new Applied<>(state, currentVersion + 1, Arrays.asList(source), metadata(state, null, null)));
  }

  /**
   * Apply my current {@code state}.
   *
   * @param state the S typed state to apply
   */
  protected void apply(final S state) {
    apply(new Applied<>(state, currentVersion + 1, Collections.emptyList(), metadata(state, null, null)));
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
   * Received by my extender when my current state has been applied and restored.
   * Must be overridden by my extender.
   *
   * @param state the S typed state
   */
  protected abstract void state(final S state);

  /**
   * Apply by setting {@code applied()} and setting state.
   *
   * @param applied the {@code Applied<S,C>} to apply
   */
  private void apply(final Applied<S, C> applied) {
    this.currentVersion = applied.stateVersion;
    this.applied(applied);
    this.state(applied.state);
  }

  private Metadata metadata(final S state, final String metadataValue, final String operation) {
    return Metadata.with(state, metadataValue == null ? "" : metadataValue, operation == null ? "" : operation);
  }
}
