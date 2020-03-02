package io.vlingo.xoom.stepflow;

/**
 * A {@link TransitionHandler} subscribes to a {@link StateTransition} and is used to perform a transaction in response
 * to a {@link StateTransition}.
 *
 * @param <T> is the source state
 * @param <R> is the target state
 * @author Kenny Bastani
 */
public class TransitionHandler<T extends State, R extends State> {

    private String address;
    private Class<?> aggregateType = Object.class;
    private StateTransition<T, R, ?> stateTransition;

    private TransitionHandler(StateTransition<T, R, ?> stateTransition) {
        this.stateTransition = stateTransition;
        this.address = stateTransition.getSourceName() + "::" + stateTransition.getTargetName();
    }

    public TransitionHandler<T, R> withAddress(String address) {
        this.address = this.address + "::" + address;
        return this;
    }

    public <A> TransitionHandler<T, R> withAggregate(Class<A> type) {
        this.aggregateType = type;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public Class<?> getAggregateType() {
        return aggregateType;
    }

    public StateTransition<T, R, ?> getStateTransition() {
        return stateTransition;
    }

    public static <T1 extends State, R1 extends State>
    TransitionHandler<T1, R1> handle(StateTransition<T1, R1, ?> stateTransition) {
        return new TransitionHandler<>(stateTransition);
    }

    public static TransitionHandler[] transitions(TransitionHandler... handlers) {
        return handlers;
    }
}
