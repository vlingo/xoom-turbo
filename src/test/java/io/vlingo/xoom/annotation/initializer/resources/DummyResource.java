// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.initializer.resources;

import io.vlingo.common.Completes;
import io.vlingo.http.Response;
import io.vlingo.xoom.annotation.autodispatch.*;
import io.vlingo.xoom.annotation.model.Dummy;
import io.vlingo.xoom.annotation.model.DummyEntity;
import io.vlingo.xoom.annotation.persistence.DummyData;
import io.vlingo.xoom.annotation.persistence.DummyQueries;
import io.vlingo.xoom.annotation.persistence.DummyQueriesActor;

import static io.vlingo.http.Method.*;

@AutoDispatch(path = "/dummies")
@Queries(protocol = DummyQueries.class, actor = DummyQueriesActor.class)
@Model(protocol = Dummy.class, actor = DummyEntity.class, data = DummyData.class)
public interface DummyResource {

    @Route(method = POST, handler = "defineWith(stage, data.name)")
    @ResponseAdapter("DummyData.from")
    Completes<Response> defineDummy(@Body DummyData data);

    @Route(method = PATCH, path = "/{dummyId}/name", handler = "withName(data.name)")
    @ResponseAdapter("DummyData.from")
    Completes<Response> changeDummyName(@Id String dummyId, @Body DummyData data);

    @Route(method = GET, handler="allDummies()")
    Completes<Response> queryDummies();

}
