package io.vlingo.xoom.annotation.autodispatch;

import io.vlingo.actors.Actor;
import io.vlingo.http.Method;

@Queries(actor = Actor.class, protocol = ActorProtocol.class)
public interface DummyQueries {

    @Route(method = Method.GET, path = "", handler = "")
    void dummyRoute();

}
