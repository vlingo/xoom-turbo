// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.annotation.persistence;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.query.StateStoreQueryActor;
import io.vlingo.xoom.symbio.store.state.StateStore;

public class OtherDummyQueriesActor extends StateStoreQueryActor implements OtherDummyQueries {

    public OtherDummyQueriesActor(final StateStore stateStore) {
        super(stateStore);
    }

    @Override
    public Completes<?> otherDummies() {
        return Completes.noTimeout();
    }

}
