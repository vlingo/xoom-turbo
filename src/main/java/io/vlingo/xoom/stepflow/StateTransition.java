package io.vlingo.xoom.stepflow;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * A {@link StateTransition} is a resource specification that defines an input state and output state, while providing
 * a validation error if an input state cannot progress to an output state.
 *
 * @param <T> is the source state
 * @param <R> is the target state
 * @author Kenny Bastani
 */
public class StateTransition<T extends State, R extends State, A> implements Transition {

    private T from;
    private R to;
    private BiConsumer<T, R> action = (a, b) -> {
    };
    private Function<A, A> aggregateConsumer = (a) -> a;

    public StateTransition(T from, R to) {
        this.from = from;
        this.to = to;
    }

    public A apply(A aggregate) {
        if (action == null) {
            throw new IllegalStateException("A state transition must define a success and error result");
        }
        A a = this.aggregateConsumer.apply(aggregate);
        this.action.accept(this.getFrom(), this.getTo());
        return a;
    }

    public void setActionHandler(BiConsumer<T, R> action) {
        this.action = action;
    }

    public void setAggregateConsumer(Function<A, A> consumer) {
        this.aggregateConsumer = consumer;
    }

    public T getFrom() {
        return from;
    }

    public R getTo() {
        return to;
    }

    @Override
    public String getSourceName() {
        return getFrom().getName();
    }

    @Override
    public String getTargetName() {
        return getTo().getName();
    }

    @Override
    public String toString() {
        return "StateTransition{" +
                "from=" + from +
                ", to=" + to +
                ", action=" + action +
                '}';
    }
}
