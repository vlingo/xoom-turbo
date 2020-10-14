// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.autodispatch;

import io.vlingo.common.Completes;
import io.vlingo.http.Response;
import io.vlingo.xoom.annotation.initializer.resources.DummyHandlers;
import io.vlingo.xoom.annotation.model.Dummy;
import io.vlingo.xoom.annotation.model.DummyEntity;
import io.vlingo.xoom.annotation.persistence.DummyData;

import static io.vlingo.http.Method.PUT;
import static io.vlingo.xoom.annotation.initializer.resources.DummyHandlers.ADAPT_STATE;
import static io.vlingo.xoom.annotation.initializer.resources.DummyHandlers.CHANGE_NAME;

@Model(protocol = Dummy.class, actor = DummyEntity.class, data = DummyData.class)
@AutoDispatch(path = "/dummies", handlers = DummyHandlers.class)
public interface DummyModelResource {

   @Route(method = PUT, path = "/{dummyId}/name", handler = CHANGE_NAME)
   @ResponseAdapter(handler = ADAPT_STATE)
    Completes<Response> changeDummyName(@Id String dummyId, @Body DummyData dummyData);

}
