// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.persistence;

import io.vlingo.xoom.lattice.model.projection.StateStoreProjectionActor;
import io.vlingo.xoom.turbo.ComponentRegistry;
import io.vlingo.xoom.turbo.annotation.initializer.infrastructure.persistence.QueryModelStateStoreProvider;
import io.vlingo.xoom.turbo.annotation.model.OtherDummyState;

public class OtherDummyProjectionActor extends StateStoreProjectionActor<OtherDummyState> {

    public OtherDummyProjectionActor() {
        super(ComponentRegistry.withType(QueryModelStateStoreProvider.class).store);
    }
}
