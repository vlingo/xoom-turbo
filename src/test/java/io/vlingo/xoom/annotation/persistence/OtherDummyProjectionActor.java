// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.persistence;

import io.vlingo.lattice.model.projection.StateStoreProjectionActor;
import io.vlingo.xoom.annotation.initializer.infrastructure.persistence.QueryModelStateStoreProvider;
import io.vlingo.xoom.annotation.model.OtherDummyState;

public class OtherDummyProjectionActor extends StateStoreProjectionActor<OtherDummyState> {

    public OtherDummyProjectionActor() {
        super(QueryModelStateStoreProvider.instance().store);
    }

}
