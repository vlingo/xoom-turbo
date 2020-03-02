package io.vlingo.xoom.stepflow;

import io.vlingo.common.Completes;

/**
 * A {@link StateHandler} is a functional interface that describes a {@link StateTransition}.
 *
 * @param <T> is the source {@link State}
 * @param <R> is the target {@link State}
 * @author Kenny Bastani
 */
public interface StateHandler<T extends State, R extends State> {
    Completes<StateTransition<T, R, ?>> execute();
}
