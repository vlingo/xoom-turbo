package io.vlingo.xoom.annotation.autodispatch;

import io.vlingo.actors.Actor;
import io.vlingo.http.Method;
import io.vlingo.lattice.model.EntityActor;

@Model(data = Actor.class, actor = EntityActor.class, protocol = ActorProtocol.class)
public interface DummyModel {

    @Route(method = Method.PUT, path = "", handler = "")
    @Response(data = "test")
     void dummyRoute();

}
