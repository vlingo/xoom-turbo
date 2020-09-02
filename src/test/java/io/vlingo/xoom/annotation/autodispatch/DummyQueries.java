package io.vlingo.xoom.annotation.autodispatch;

import io.vlingo.actors.Actor;

@Queries(actor = Actor.class, protocol = ActorProtocol.class)
public interface DummyQueries {

}
