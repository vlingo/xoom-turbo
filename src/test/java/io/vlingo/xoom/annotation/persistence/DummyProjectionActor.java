// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.persistence;

import io.vlingo.lattice.model.projection.StateStoreProjectionActor;
import io.vlingo.symbio.*;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.xoom.annotation.model.DummyState;

public class DummyProjectionActor extends StateStoreProjectionActor<DummyState> {
    public DummyProjectionActor(StateStore stateStore) {
        super(stateStore);
    }

    public DummyProjectionActor(StateStore stateStore, StateAdapter<Object, State<?>> stateAdapter, EntryAdapter<Source<?>, Entry<?>> entryAdapter) {
        super(stateStore, stateAdapter, entryAdapter);
    }

//    public DummyProjectionActor() {
//        super(QueryModelStateStoreProvider.instance().store);
//    }

}
