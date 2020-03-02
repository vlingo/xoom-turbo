package io.vlingo.xoom.stepflow;

import io.vlingo.common.Completes;

/**
 * A functional interface that transforms a {@link StateTransition} into a {@link Completes}.
 *
 * @param <T> is the source {@link State}
 * @param <R> is the target {@link State}
 * @author Kenny Bastani
 */
@FunctionalInterface
public interface CompletesState<T extends State, R extends State> {
    void apply(StateTransition<T, ?, ?> transition, R state);
}
