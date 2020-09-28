// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.autodispatch;

import io.vlingo.common.Completes;
import io.vlingo.http.Method;
import io.vlingo.http.Response;
import io.vlingo.xoom.annotation.initializer.resources.DummyHandlers;
import io.vlingo.xoom.annotation.persistence.DummyQueries;
import io.vlingo.xoom.annotation.persistence.DummyQueriesActor;

import static io.vlingo.xoom.annotation.initializer.resources.DummyHandlers.ADAPT_STATE;
import static io.vlingo.xoom.annotation.initializer.resources.DummyHandlers.QUERY_ALL;

@Queries(protocol = DummyQueries.class, actor = DummyQueriesActor.class)
@AutoDispatch(path = "/dummies", handlers = DummyHandlers.class)
public interface DummyQueriesResource {

    @Route(method = Method.GET, path = "any-path", handler = QUERY_ALL)
    @ResponseAdapter(handler = ADAPT_STATE)
    Completes<Response> dummyRouteForQueries();

}
