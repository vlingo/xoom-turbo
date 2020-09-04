package io.vlingo.xoom.annotation.autodispatch;

import io.vlingo.http.Method;
import io.vlingo.lattice.model.EntityActor;

@Model(actor = EntityActor.class, protocol = DummyModel.class, data = DummyModel.class)
public interface DummyModel {

    @Route(method = Method.PUT, path = "", handler = "")
     void dummyRoute();

}
