package io.vlingo.xoom.stepflow;

import io.vlingo.actors.Actor;
import io.vlingo.common.Completes;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The default {@link Kernel} {@link Actor} implementation.
 *
 * @author Kenny Bastani
 * @see Actor
 * @see Kernel
 * @see StepFlow
 */
public class KernelActor extends Actor implements Kernel {
    private final Map<String, TransitionHandler> transitionHandlerMap;
    private final Map<String, State<? extends State>> stateMap;
    private String kernelName = "DefaultProcessorKernel";

    public KernelActor() {
        transitionHandlerMap = new HashMap<>();
        stateMap = new HashMap<>();
    }

    @Override
    public Completes<String> getName() {
        return completes().with(this.kernelName);
    }

    @Override
    public void setName(String name) {
        this.kernelName = name;
    }

    @Override
    public void registerStates(State<? extends State>... states) {
        Stream.of(states).forEach(s -> {
            if (stateMap.containsKey(s.getName())) {
                throw new IllegalStateException("The state with the name " + s.getName() + " has " +
                        "already been registered");
            }
            Stream.of(s.getTransitionHandlers()).forEach(transitionHandler -> {
                transitionHandlerMap.compute(transitionHandler.getAddress(), (a, b) -> {
                    TransitionHandler result = b;
                    if (result != null) {
                        throw new IllegalStateException("The state transition for " + a + " is already registered");
                    } else {
                        logger().info("State transition [" + a + "] was registered with " + this.kernelName);
                        result = transitionHandler;
                    }
                    return result;
                });
            });
            stateMap.put(s.getName(), s);
        });
    }

    @Override
    public Completes<List<State<? extends State>>> getStates() {
        return completes().with(Collections.unmodifiableList(new ArrayList<>(stateMap.values())));
    }

    @Override
    public Completes<List<StateTransition>> getStateTransitions() {
        return completes().with(Collections.unmodifiableList(transitionHandlerMap.values().stream()
                .map(TransitionHandler::getStateTransition)
                .collect(Collectors.toList())));
    }

    @Override
    public <T extends Event> Completes<StateTransition> applyEvent(T event) {
        TransitionHandler handler = transitionHandlerMap.get(event.getEventType());
        try {
            if (handler == null)
                throw new RuntimeException("The event with type [" + event.getEventType() + "] does not match a" +
                        " valid transition handler in the processor kernel.");
            return completes().with(handler.getStateTransition());
        } catch (Exception ex) {
            logger().debug(ex.getMessage(), ex);
            return completes().with(null);
        }
    }

    @Override
    public Completes<Map<String, TransitionHandler>> getTransitionMap() {
        return completes().with(transitionHandlerMap);
    }
}
