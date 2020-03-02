package io.vlingo.xoom.management.endpoints;

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.management.endpoint.annotation.Endpoint;
import io.micronaut.management.endpoint.annotation.Read;
import io.vlingo.xoom.events.FlowCreatedEvent;
import io.vlingo.xoom.stepflow.*;

import java.util.*;
import java.util.stream.Stream;

@Endpoint(id = "flows", prefix = "custom", defaultEnabled = true, defaultSensitive = false)
public class StepFlowEndpoint implements ApplicationEventListener<FlowCreatedEvent> {
    private Map<String, StepFlow> stepFlow = new HashMap<>();

    @Read
    public Map<String, Object> getMap(String flowName) {
        Set<String> nodes = new HashSet<>();
        Map<String, Integer> portCount = new HashMap<>();
        Set<Map<String, Object>> links = new HashSet<>();
        Map<String, Object> results = new HashMap<>();

        Kernel kernel = stepFlow.get(flowName).getKernel().await();
        List<State> states = kernel.getStates().await();

        states.forEach(state -> {
            nodes.add(state.getName().split("::")[0]);
        });

        states.stream().map(state -> Stream.of(state.getTransitionHandlers()))
                .flatMap(handler -> handler)
                .forEach(handler -> defineGraph(portCount, links, handler));

        results.put("nodes", nodes);
        results.put("edges", links);
        results.put("flow", stepFlow.get(flowName).getName().await());

        return results;
    }

    private void defineGraph(Map<String, Integer> portCount, Set<Map<String, Object>> links, TransitionHandler handler) {
        Map<String, Object> link = new HashMap<>();

        // Split the handler address into parts
        String[] address = handler.getAddress().split("::");

        link.put("source", handler.getStateTransition().getSourceName());
        link.put("target", handler.getStateTransition().getTargetName());

        // If the state transition is logical then return TO otherwise return the sub-state transition's name
        link.put("label", address.length == 3 ? address[2] : "TO");

        // (i=0)-[i++]->(port=[j+i])<-[j++]-(j=0)

        // Algorithmically determines whether or not to place a label left or right of an arrow

        // Calculates a port based on the number of incoming connections spanning from the source node
        Integer sourcePort = address.length != 3 ?
                portCount.compute(handler.getStateTransition().getSourceName(),
                        (s, integer) -> Optional.ofNullable(integer)
                                .map(i -> i + 1)
                                .orElse(0)) : 1;

        // Calculates a port based on the number of outgoing connections spanning into the target node
        Integer targetPort = address.length != 3 ?
                portCount.compute("TO" + "::" + handler.getStateTransition().getTargetName(),
                        (s, integer) -> Optional.ofNullable(integer)
                                .map(i -> i + 1)
                                .orElse(1)) : 0;

        // Calculate and set the port on the response by summing the results of the sourcePort and targetPort
        link.put("port", sourcePort + targetPort);

        links.add(link);
    }

    @Override
    public void onApplicationEvent(FlowCreatedEvent event) {
        stepFlow.put(event.getFlowName(), event.getSource());
    }
}
