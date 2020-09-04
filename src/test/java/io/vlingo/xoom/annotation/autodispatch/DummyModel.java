package io.vlingo.xoom.annotation.autodispatch;

import io.vlingo.actors.Actor;
import io.vlingo.lattice.model.EntityActor;

@Model(data = Actor.class, actor = EntityActor.class, protocol = ActorProtocol.class)
public interface DummyModel {

}
