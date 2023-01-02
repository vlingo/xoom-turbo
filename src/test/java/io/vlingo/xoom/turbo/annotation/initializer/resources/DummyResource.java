// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.annotation.initializer.resources;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.turbo.annotation.autodispatch.*;
import io.vlingo.xoom.turbo.annotation.model.Dummy;
import io.vlingo.xoom.turbo.annotation.model.DummyEntity;
import io.vlingo.xoom.turbo.annotation.persistence.DummyData;
import io.vlingo.xoom.turbo.annotation.persistence.DummyQueries;
import io.vlingo.xoom.turbo.annotation.persistence.DummyQueriesActor;

import static io.vlingo.xoom.http.Method.*;
import static io.vlingo.xoom.turbo.annotation.initializer.resources.DummyHandlers.*;

@AutoDispatch(path = "/dummies", handlers = DummyHandlers.class)
@Queries(protocol = DummyQueries.class, actor = DummyQueriesActor.class)
@Model(protocol = Dummy.class, actor = DummyEntity.class, data = DummyData.class)
public interface DummyResource {

    @Route(method = POST, handler = DEFINE_WITH)
    @ResponseAdapter(handler = ADAPT_STATE)
    Completes<Response> defineDummy(@Body DummyData dummyData);

    @Route(method = PATCH, path = "/{id}/name", handler = CHANGE_NAME)
    @ResponseAdapter(handler = ADAPT_STATE)
    Completes<Response> changeDummyName(@Id String id, @Body DummyData dummyData);

    @Route(method = GET, handler= QUERY_ALL)
    Completes<Response> queryDummies();

    @Route(method = GET, path = "/{id}")
    default Completes<Response> queryById(@Id String id) {
        return Completes.withSuccess(Response.of(Response.Status.Ok, "[{}]"));
    }

}
