// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.stepflow;

import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.actors.Message;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.common.Scheduled;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@link FlowActor} is the default {@link Actor} implementation for a {@link StepFlow}.
 */
@SuppressWarnings("rawtypes")
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
