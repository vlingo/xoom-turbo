package io.vlingo.xoom.stepflow;

import io.vlingo.actors.Actor;
import io.vlingo.actors.Definition;
import io.vlingo.actors.Stage;
import io.vlingo.actors.Stoppable;
import io.vlingo.common.Completes;

import java.util.List;

/**
 * <p>A {@link StepFlow} is a distributed task executor that dereferences actors to a lower-level library. Processors
 * are provided with a set of handlers that are exposed by a defined collection of protocols. Handlers are
 * address-referencable functions that are invoked through a protocol definition. Processors manage a registry of
 * handlers that pipe together other processor implementations. Processor dispatchers are discoverable through a
 * processor registry. A processor registry can advertise dispatcher functions across the boundary of the network.
 * </p>
 *
 * <p>
 * Dispatchers are a collection of contracts made between a producing processor subscribing processors. Subscribing
 * processors negotiate a lease on a remote dispatcher. Dispatchers are responsible for pushing events to
 * subscribers. An application with a processor implementation advertises the health of a handler by its port
 * definition and URI. Handlers provide health statuses to a service registry to adjust backpressure on the producer.
 * </p>
 *
 * <p>
 * Processors connect handlers to a kernel definition.
 * </p>
 *
 * <p>
 * When state mutations are applied to the underlying store, the storage mechanism must store the message in the same
 * transaction as the mutation. Application-level dual-writes cannot guarantee the availability of two separate
 * storage mechanisms that are uncoordinated across the boundary of a network. Two transactions cannot
 * consistently coordinate commits across two different systems that do not share a unified state. Transactional scopes
 * must be embedded, which means one commit will happen before the other. If the first succeeds and the second fails,
 * the system is now in an unrecoverable inconsistent state. Which means that the message log will either re-enqueue an
 * unacknowledged message after the database write was successful, or a message will be acknowledged before the database
 * write failed. To guarantee that all state mutations are stored together with the message that carried the state
 * transfer, a single ACID database should store these together in the same transaction.
 * </p>
 *
 * <p>
 * When a message is stored together with the mutated aggregate in a single transaction, the processor can
 * acknowledge the message receipt back to the broker. If the application crashes, the message cannot be
 * reprocessed since the version of the entity has been incremented. When the message is redelivered to a processor,
 * it will be rejected by the kernel for having a stale context.
 * </p>
 *
 * <p>
 * To ensure that subscribers will be durably notified without having to poll storage from across multiple nodes using a
 * distributed lock, it's recommended that the underlying store be able to use CDC to replicate the durable storage of
 * a state mutation as a new event message. The event message should be sent to a durable queue that is routed to the
 * originating processor so that it can be forked and broadcast to handlers listed in the router registry. When the
 * event is received by the originating processor, a router is used to fork event messages into separate channels that
 * correspond to remote handlers that have subscribed to the central router registry. The originating processor will
 * then create a copy of the event message and place it in an ordered queue that is dispatched to a remote handler
 * using an advertised HTTP endpoint. Once the push to the subscriber is acknowledged, the producer will iterate to
 * the next message until all available messages have been routed to subscribing consumers.
 * </p>
 *
 * <p>
 * When messages are pushed to subscribers, a processor will handle the message by durably pushing the event to its
 * mailbox before acknowledgement of a receipt to the producer. Duplicate messages can be received only in the order
 * they are produced, which means that duplicate messages can never increment the version of an entity more than once,
 * therefore, all duplicates will be rejected and sent to the dead letter queue on arrival to the kernel. This means
 * that any failure in the network or application will eventually be acknowledged without loss of data or a duplicate
 * event being accepted by a consumer's kernel.
 * </p>
 *
 * <p>
 * Backpressure can be applied by consumers as a part of the subscription to a producer using server-sent events.
 * </p>
 *
 * @author Kenny Bastani
 */
public interface StepFlow extends Stoppable {

    Completes<Boolean> shutDown();

    Completes<Boolean> startUp();

    Completes<Kernel> getKernel();

    Completes<String> getName();

    Completes<StateTransition> applyEvent(Event event);

    static <A extends Actor> StepFlow startWith(Stage stage, Class<A> clazz, String actorName) {
        return startWith(stage, clazz, actorName, Definition.NoParameters);
    }

    static <A extends Actor, P extends StepFlow> P startWith(Stage stage, Class<A> clazz, Class<P> protocol,
                                                             String actorName, List<Object> params) {
        P processor = stage.actorFor(protocol, Definition.has(
                clazz,
                params,
                "queueMailbox", actorName),
                stage.world().addressFactory().withHighId(),
                stage.world().defaultLogger());

        processor.startUp();
        return processor;
    }

    @SuppressWarnings("unchecked")
    public static <A extends Actor, P> StepFlow startWith(Stage stage, Class<A> clazz, String actorName,
                                                          List<Object> params) {
        StepFlow processor = stage.actorFor(StepFlow.class, Definition.has(
                clazz,
                params,
                "queueMailbox", actorName),
                stage.world().addressFactory().withHighId(),
                stage.world().defaultLogger());

        processor.startUp();
        return processor;
    }
}
