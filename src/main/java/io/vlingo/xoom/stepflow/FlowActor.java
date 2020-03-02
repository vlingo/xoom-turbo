package io.vlingo.xoom.stepflow;

import io.vlingo.actors.Actor;
import io.vlingo.actors.Message;
import io.vlingo.common.Completes;
import io.vlingo.common.Scheduled;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@link FlowActor} is the default {@link Actor} implementation for a {@link StepFlow}.
 *
 * @author Kenny Bastani
 */
public abstract class FlowActor extends Actor implements StepFlow, Scheduled<Message> {

    private final List<State> states;
    private Kernel kernel;

    public FlowActor() {
        states = new ArrayList<>();
    }

    protected FlowActor(List<State> states) {
        this.states = states;
    }

    @Override
    public Completes<Boolean> shutDown() {
        stop();
        return completes().with(true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Completes<Boolean> startUp() {
        logger().info("Starting " + this.definition().actorName() + "...");
        //stage().scheduler().schedule(selfAs(Scheduled.class), null, 0L, 10);
        this.kernel = stage().actorFor(Kernel.class, KernelActor.class);
        this.kernel.setName(this.definition().actorName() + "/Kernel");
        this.kernel.registerStates(states.toArray(new State[]{}));
        return completes().with(true);
    }

    @Override
    public Completes<Kernel> getKernel() {
        if (kernel != null) {
            return completes().with(kernel);
        } else {
            throw new IllegalStateException("The processor's kernel has not been initialized.");
        }
    }

    @Override
    public Completes<StateTransition> applyEvent(Event event) {
        return Completes.withSuccess((StateTransition) completes()
                .with(this.kernel.applyEvent(event).await()).outcome());
    }

    @Override
    public Completes<String> getName() {
        return completes().with("Default Processor");
    }

    @Override
    public void intervalSignal(Scheduled scheduled, Message data) {
    }
}
