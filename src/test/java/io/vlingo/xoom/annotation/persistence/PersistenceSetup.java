// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.persistence;

import io.vlingo.symbio.State.TextState;
import io.vlingo.xoom.annotation.persistence.model.Dummy;
import io.vlingo.xoom.annotation.persistence.model.DummyState;
import io.vlingo.xoom.annotation.persistence.model.OtherDummy;
import io.vlingo.xoom.annotation.persistence.model.OtherDummyState;

import static io.vlingo.xoom.annotation.persistence.Persistence.StorageType.STATE_STORE;
import static io.vlingo.xoom.annotation.persistence.Projections.ProjectionType.EVENT_BASED;

@Persistence(basePackage = "io.vlingo.xoom.annotation", storageType = STATE_STORE, cqrs = true)
@StateAdapters(values = {
        @StateAdapter(from = DummyState.class, to= TextState.class),
        @StateAdapter(from = OtherDummyState.class, to= TextState.class)
})
public class PersistenceSetup {


}
