package io.vlingo.xoom.events;

import io.micronaut.context.event.ApplicationEvent;
import io.vlingo.xoom.stepflow.StepFlow;

public class FlowCreatedEvent extends ApplicationEvent {
    private final StepFlow source;
    private final String flowName;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @param flowName The name of the flow.
     * @throws IllegalArgumentException if source is null.
     */
    public FlowCreatedEvent(StepFlow source, String flowName) {
        super(source);
        this.source = source;
        this.flowName = flowName;
    }

    @Override
    public StepFlow getSource() {
        return source;
    }

    public String getFlowName() {
        return flowName;
    }
}
