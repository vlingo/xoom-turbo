// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.scooter.model;

import io.vlingo.xoom.symbio.Metadata;
import io.vlingo.xoom.symbio.Source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A holder for {@code Source<T>}, {@code Metadata}, and snapshot.
 * <p>
 * Warning: Using {@code alongWith(state, sources, metadata)} will replace
 * the previous {@code metadata}. If this is undesirable then don't use the
 * {@code alongWith(state, sources, metadata)} or merge the previous {@code metadata}
 * with the new {@code metadata}.
 *
 * @param <S> the type of state
 * @param <C> the type of Source
 */
public class Applied<S, C> {
  public final Metadata metadata;
  public final S state;
  public final int stateVersion;

  private List<Source<C>> sources;

  /**
   * Construct my state.
   *
   * @param state        the S state of the entity
   * @param stateVersion the int version of the entity state
   * @param sources      the {@code List<Source<C>>} of DomainEvent or Command instances
   * @param metadata     the Metadata associated with this state and stateVersion
   */
  public Applied(final S state, final int stateVersion, final List<Source<C>> sources, final Metadata metadata) {
    this.sources = sources;
    this.metadata = metadata;
    this.state = state;
    this.stateVersion = stateVersion;
  }

  public Applied(final S state, final List<Source<C>> sources, final Metadata metadata) {
    this(state, 1, sources, metadata);
  }

  public Applied(final int stateVersion, final List<Source<C>> sources, final Metadata metadata) {
    this(null, stateVersion, sources, metadata);
  }

  public Applied(final List<Source<C>> sources, final Metadata metadata) {
    this(null, 1, sources, metadata);
  }

  public Applied(final int stateVersion, final List<Source<C>> sources) {
    this(null, stateVersion, sources, Metadata.nullMetadata());
  }

  public Applied(final List<Source<C>> sources) {
    this(null, 1, sources, Metadata.nullMetadata());
  }

  public Applied() {
    this(null, 0, Collections.emptyList(), Metadata.nullMetadata());
  }

  public Applied<S, C> alongWith(final List<Source<C>> sources) {
    return alongWith(state, sources, metadata);
  }

  public Applied<S, C> alongWith(final int stateVersion, final List<Source<C>> sources) {
    return alongWith(state, stateVersion, sources, metadata);
  }

  public Applied<S, C> alongWith(final S state, final int stateVersion, final List<Source<C>> sources) {
    return alongWith(state, stateVersion, sources, metadata);
  }

  public Applied<S, C> alongWith(final S state, final List<Source<C>> sources) {
    return alongWith(state, sources, metadata);
  }

  public Applied<S, C> alongWith(final S state, final List<Source<C>> sources, final Metadata metadata) {
    final List<Source<C>> all = new ArrayList<>(this.sources);
    all.addAll(sources);
    return new Applied<>(state, stateVersion, all, metadata);
  }

  public Applied<S, C> alongWith(final S state, final int stateVersion, final List<Source<C>> sources, final Metadata metadata) {
    final List<Source<C>> all = new ArrayList<>(this.sources);
    all.addAll(sources);
    return new Applied<>(state, stateVersion, all, metadata);
  }

  public int size() {
    return sources.size();
  }


  public List<Source<C>> sources() {
    return sources;
  }

  public List<Source<C>> sourcesForTest() {
    sources = new ArrayList<>(0);
    return sources;
  }

  public Source<C> sourceAt(final int index) {
    return sources.get(index);
  }

  public Class<?> sourceTypeAt(final int index) {
    return sources.get(index).getClass();
  }

  public Class<?> stateType() {
    return state.getClass();
  }

  public String stateTypeName() {
    return state.getClass().getName();
  }

  public String stateTypeSimpleName() {
    return state.getClass().getSimpleName();
  }
}
