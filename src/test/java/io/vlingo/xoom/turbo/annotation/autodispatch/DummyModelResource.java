// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.autodispatch;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.turbo.annotation.initializer.resources.DummyHandlers;
import io.vlingo.xoom.turbo.annotation.model.Dummy;
import io.vlingo.xoom.turbo.annotation.model.DummyEntity;
import io.vlingo.xoom.turbo.annotation.persistence.DummyData;

import static io.vlingo.xoom.http.Method.PUT;
import static io.vlingo.xoom.turbo.annotation.initializer.resources.DummyHandlers.ADAPT_STATE;
import static io.vlingo.xoom.turbo.annotation.initializer.resources.DummyHandlers.CHANGE_NAME;

@Model(protocol = Dummy.class, actor = DummyEntity.class, data = DummyData.class)
@AutoDispatch(path = "/dummies/", handlers = DummyHandlers.class)
public interface DummyModelResource {

   @Route(method = PUT, path = "/{id}/name/", handler = CHANGE_NAME)
   @ResponseAdapter(handler = ADAPT_STATE)
    Completes<Response> changeDummyName(@Id String id, @Body DummyData dummyData);

}
