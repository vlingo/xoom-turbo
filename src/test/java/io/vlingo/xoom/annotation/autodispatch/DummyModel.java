package io.vlingo.xoom.annotation.autodispatch;

import io.vlingo.http.Method;
import io.vlingo.lattice.model.EntityActor;

@Model(data = ModelData.class, actor = EntityActor.class, protocol = ActorProtocol.class)
public interface DummyModel {

    @Route(method = Method.PUT, path = "", handler = "someMethod(obj1, data.message, obj2)")
    @ResponseAdapter("test")
    void dummyRouteForModel(@Body String body);

}
