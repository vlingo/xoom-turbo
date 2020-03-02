package io.vlingo.xoom.events;

import io.micronaut.context.event.ApplicationEvent;
import io.vlingo.xoom.server.VlingoScene;

public class SceneStartedEvent extends ApplicationEvent {

    private final VlingoScene source;

    public SceneStartedEvent(VlingoScene source) {
        super(source);
        this.source = source;
    }

    @Override
    public VlingoScene getSource() {
        return source;
    }
}
