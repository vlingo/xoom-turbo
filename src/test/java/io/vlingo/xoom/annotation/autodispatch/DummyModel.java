// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.autodispatch;

import io.vlingo.http.Method;
import io.vlingo.lattice.model.EntityActor;
import io.vlingo.xoom.annotation.initializer.resources.DummyHandlers;

@Model(data = ModelData.class, actor = EntityActor.class, protocol = ActorProtocol.class)
@AutoDispatch(path = "/dummies", handlers = DummyHandlers.class)
public interface DummyModel {

    @Route(method = Method.PUT, path = "/any-path", handler = 0)
    @ResponseAdapter(handler = 0)
    void dummyRouteForModel(@Body String body);

}
