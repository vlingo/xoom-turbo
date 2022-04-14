// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.persistence;

import io.vlingo.xoom.turbo.annotation.model.*;

import static io.vlingo.xoom.turbo.annotation.persistence.Persistence.StorageType.STATE_STORE;

@Persistence(basePackage = "io.vlingo.xoom.turbo.annotation", storageType = STATE_STORE, cqrs = true)
@Projections({
        @Projection(actor = DummyProjectionActor.class, becauseOf = {DummyCreated.class, DummyCancelled.class}),
        @Projection(actor = OtherDummyProjectionActor.class, becauseOf = {OtherDummyPromoted.class})
})
@EnableQueries({
        @QueriesEntry(protocol = DummyQueries.class, actor = DummyQueriesActor.class),
        @QueriesEntry(protocol = OtherDummyQueries.class, actor = OtherDummyQueriesActor.class),
})
@Adapters({DummyState.class, OtherDummyState.class})
@DataObjects({DummyData.class, OtherDummyData.class})
public class PersistenceSetup {


}


