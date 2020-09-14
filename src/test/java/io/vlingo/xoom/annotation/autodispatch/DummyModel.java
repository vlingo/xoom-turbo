// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.autodispatch;

import io.vlingo.http.Method;
import io.vlingo.lattice.model.EntityActor;

@Model(data = ModelData.class, actor = EntityActor.class, protocol = ActorProtocol.class)
public interface DummyModel {

    @Route(method = Method.PUT, path = "", handler = "someMethod(obj1, data.message, obj2)")
    @ResponseAdapter("test")
    void dummyRouteForModel(@Body String body);

}
