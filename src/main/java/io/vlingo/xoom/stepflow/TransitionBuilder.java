package io.vlingo.xoom.stepflow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class TransitionBuilder<T extends State, R extends State, A> {

    private T source;
    private R target;
    private List<BiConsumer<T, R>> actions = new ArrayList<>();

    private TransitionBuilder(T source) {
        this.source = source;
    }

    private TransitionBuilder(T source, R target) {
        this.source = source;
        this.target = target;
    }

    public <R1 extends State> TransitionBuilder2<T, R1, ?> to(R1 target) {
        return new TransitionBuilder2<>(source, target);
    }

    public static <T1 extends State> TransitionBuilder<T1, ?, ?> from(T1 source) {
        return new TransitionBuilder<>(source);
    }

    public static class TransitionBuilder2<T2 extends State, R2 extends State, A> {
        private T2 source;
        private R2 target;

        private TransitionBuilder2(T2 source, R2 target) {
            this.source = source;
            this.target = target;
        }

        public <A2> TransitionBuilder3<T2, R2, A2> on(Class<A2> aggregateType) {
            return new TransitionBuilder3<T2, R2, A2>(source, target, aggregateType);
        }

        public StateTransition<T2, R2, A> then(BiConsumer<T2, R2> action) {
            StateTransition<T2, R2, A> transition = new StateTransition<>(source, target);
            transition.setActionHandler(action);
            return transition;
        }
    }

    public static class TransitionBuilder3<T2 extends State, R2 extends State, A> {
        private T2 source;
        private R2 target;
        private Class<A> aggregateType;
        private Function<A, A> action;

        private TransitionBuilder3(T2 source, R2 target, Class<A> aggregateType) {
            this.source = source;
            this.target = target;
            this.aggregateType = aggregateType;
        }

        public TransitionBuilder4<T2, R2, A> then(Function<A, A> aggregateConsumer) {
            this.action = aggregateConsumer;
            StateTransition<T2, R2, A> transition = new StateTransition<T2, R2, A>(source, target);
            transition.setAggregateConsumer(aggregateConsumer);
            return new TransitionBuilder4<T2, R2, A>(this);
        }

        public StateTransition<T2, R2, ?> andThenAccept(BiConsumer<T2, R2> consumer) {
            StateTransition<T2, R2, ?> transition = new StateTransition<>(source, target);
            transition.setActionHandler(consumer);
            return transition;
        }
    }

    public static class TransitionBuilder4<T2 extends State, R2 extends State, A> {
        private TransitionBuilder3<T2, R2, A> transitionBuilder3;

        private TransitionBuilder4(TransitionBuilder3<T2, R2, A> transitionBuilder3) {
            this.transitionBuilder3 = transitionBuilder3;
        }

        public StateTransition<T2, R2, A> then(BiConsumer<T2, R2> consumer) {
            StateTransition<T2, R2, A> transition =
                    new StateTransition<T2, R2, A>(transitionBuilder3.source, transitionBuilder3.target);
            transition.setAggregateConsumer(transitionBuilder3.action);
            transition.setActionHandler(consumer);
            return transition;
        }
    }
}
