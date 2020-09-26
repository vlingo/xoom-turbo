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
import static io.vlingo.xoom.annotation.initializer.resources.DummyHandlers.*;

@AutoDispatch(path = "/dummies", handlers = DummyHandlers.class)
@Queries(protocol = DummyQueries.class, actor = DummyQueriesActor.class)
@Model(protocol = Dummy.class, actor = DummyEntity.class, data = DummyData.class)
public interface DummyResource {

    @Route(method = POST, handler = DEFINE_WITH)
    @ResponseAdapter(handler = ADAPT_STATE)
    Completes<Response> defineDummy(@Body DummyData dummyData);

    @Route(method = PATCH, path = "/{dummyId}/name", handler = CHANGE_NAME)
    @ResponseAdapter(handler = ADAPT_STATE)
    Completes<Response> changeDummyName(@Id String dummyId, @Body DummyData dummyData);

    @Route(method = GET, handler= QUERY_ALL)
    Completes<Response> queryDummies();

}
