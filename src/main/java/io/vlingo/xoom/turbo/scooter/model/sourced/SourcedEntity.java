// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.scooter.model.sourced;

import io.vlingo.xoom.symbio.Metadata;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.turbo.scooter.model.Applied;
import io.vlingo.xoom.turbo.scooter.model.Entity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public abstract class SourcedEntity<T> extends Entity<Object,T> {
  private static final Map<Class<SourcedEntity<Source<?>>>,Map<Class<Source<?>>, BiConsumer<SourcedEntity<?>, Source<?>>>> registeredConsumers =
          new ConcurrentHashMap<>();

  private final int currentVersion;

  /**
   * Register the means to apply {@code sourceType} instances for state transition
   * of {@code sourcedType} by means of a given {@code consumer}.
   * @param sourcedType the concrete {@code Class<SOURCED>} type to which sourceType instances are applied
   * @param sourceType the concrete {@code Class<SOURCE>} type to apply
   * @param consumer the {@code BiConsumer<SOURCED, SOURCE>} used to perform the application of sourceType
   * @param <SOURCED> the type {@code <? extends Sourced<?>>} of the sourced entity to apply to
   * @param <SOURCE> the type {@code <? extends Source<?>>} of the source to be applied
   */
  @SuppressWarnings("unchecked")
  public static <SOURCED extends SourcedEntity<?>, SOURCE extends Source<?>> void registerConsumer(
          final Class<SOURCED> sourcedType,
          final Class<SOURCE> sourceType,
          final BiConsumer<SOURCED, SOURCE> consumer) {

    Map<Class<Source<?>>, BiConsumer<SourcedEntity<?>, Source<?>>> sourcedTypeMap =
            registeredConsumers.get(sourcedType);

    if (sourcedTypeMap == null) {
      sourcedTypeMap = new ConcurrentHashMap<>();
      registeredConsumers.put((Class<SourcedEntity<Source<?>>>) sourcedType, sourcedTypeMap);
    }

    sourcedTypeMap.put((Class<Source<?>>) sourceType, (BiConsumer<SourcedEntity<?>, Source<?>>) consumer);
  }

  @Override
  public int currentVersion() {
    return currentVersion;
  }

  /**
   * Answer my type name.
   * @return String
   */
  public String type() {
    return getClass().getSimpleName();
  }

  /**
   * Construct my default state.
   */
  protected SourcedEntity() {
    this.currentVersion = 0;
  }

  /**
   * Construct my default state.
   * @param stream the {@code List<Source<T>>} with which to initialize my state
   * @param currentVersion the int to set as my currentVersion
   */
  protected SourcedEntity(final List<Source<T>> stream, final int currentVersion) {
    this.currentVersion = currentVersion;

    transitionWith(stream);
  }

  /**
   * Apply all of the given {@code sources} to myself, which includes appending
   * them to my journal and reflecting the representative changes to my state.
   * @param sources the {@code List<Source<T>>} to apply
   */
  final protected void apply(final List<Source<T>> sources) {
    apply(sources, metadata());
  }

  /**
   * Apply all of the given {@code sources} to myself along with {@code metadata}.
   * @param sources the {@code List<Source<T>>} to apply
   * @param metadata the Metadata to apply along with source
   */
  final protected void apply(final List<Source<T>> sources, final Metadata metadata) {
    applyWithTransition(new Applied<>(snapshot(), nextVersion(), sources, metadata));
  }

  /**
   * Apply the given {@code source} to myself.
   * @param source the {@code Source<T>} to apply
   */
  final protected void apply(final Source<T> source) {
    apply(source, metadata());
  }

  /**
   * Apply the given {@code source} to myself with {@code metadata}.
   * @param source the {@code Source<T>} to apply
   * @param metadata the Metadata to apply along with source
   */
  final protected void apply(final Source<T> source, final Metadata metadata) {
    applyWithTransition(new Applied<>(snapshot(), nextVersion(), wrap(source), metadata));
  }

  /**
   * Answer a {@code List<Source<T>>} from the varargs {@code sources}.
   * @param sources the varargs {@code Source<T>} of sources to answer as a {@code List<Source<T>>}
   * @return {@code List<Source<T>>}
   */
  @SuppressWarnings("unchecked")
  protected List<Source<T>> asList(final Source<T>... sources) {
    return Arrays.asList(sources);
  }

  /**
   * Answer my {@code Metadata}.
   * Must override if {@code Metadata} is to be supported.
   * @return Metadata
   */
  protected Metadata metadata() {
    return Metadata.nullMetadata();
  }

  /**
   * Answer a valid {@code SNAPSHOT} state instance if a snapshot should
   * be taken and persisted along with applied {@code Source<T>} instance(s).
   * Must override if snapshots are to be supported.
   * @param <SNAPSHOT> the type of the snapshot
   * @return {@code SNAPSHOT}
   */
  protected <SNAPSHOT> SNAPSHOT snapshot() {
    return null;
  }

  /**
   * Answer my stream name. Must override.
   * @return String
   */
  protected abstract String streamName();

  /**
   * Answer a representation of a number of segments as a
   * composite stream name. The implementor of {@code streamName()}
   * would use this method if the its stream name is built from segments.
   * @param separator the String separator the insert between segments
   * @param streamNameSegments the varargs String of one or more segments
   * @return String
   */
  protected String streamNameFrom(final String separator, final String... streamNameSegments) {
    final StringBuilder builder = new StringBuilder();
    builder.append(streamNameSegments[0]);
    for (int idx = 1; idx < streamNameSegments.length; ++idx) {
      builder.append(separator).append(streamNameSegments[idx]);
    }
    return builder.toString();
  }

  private void applyWithTransition(final Applied<Object,T> applied) {
    applied(applied);

    transitionWith(applied.sources());
  }

  private void transitionWith(final List<Source<T>> stream) {

    for (final Source<?> source : stream) {
      Class<?> type = getClass();

      BiConsumer<SourcedEntity<?>, Source<?>> consumer = null;

      while (type != SourcedEntity.class) {
        final Map<Class<Source<?>>, BiConsumer<SourcedEntity<?>, Source<?>>> sourcedTypeMap =
                registeredConsumers.get(type);

        if (sourcedTypeMap != null) {
          consumer = sourcedTypeMap.get(source.getClass());
          if (consumer != null) {
            consumer.accept(this, source);
            break;
          }
        }

        type = type.getSuperclass();
      }

      if (consumer == null) {
        throw new IllegalStateException("No such Sourced type.");
      }
    }
  }

  /**
   * Answer {@code source} wrapped in a {@code List<Source<T>>}.
   * @param source the {@code Source<T>} to wrap
   * @return {@code List<Source<T>>}
   */
  private List<Source<T>> wrap(final Source<T> source) {
    return Arrays.asList(source);
  }

  /**
   * Answer {@code sources} wrapped in a {@code List<Source<T>>}.
   * @param sources the {@code Source<T>[]} to wrap
   * @return {@code List<Source<T>>}
   */
  @SuppressWarnings("unused")
  private List<Source<T>> wrap(final Source<T>[] sources) {
    return Arrays.asList(sources);
  }
}
