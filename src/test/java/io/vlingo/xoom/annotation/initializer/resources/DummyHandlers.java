// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer.resources;

import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;
import io.vlingo.xoom.annotation.autodispatch.Handler.Three;
import io.vlingo.xoom.annotation.autodispatch.Handler.Two;
import io.vlingo.xoom.annotation.autodispatch.HandlerEntry;
import io.vlingo.xoom.annotation.model.Dummy;
import io.vlingo.xoom.annotation.model.DummyState;
import io.vlingo.xoom.annotation.persistence.DummyData;
import io.vlingo.xoom.annotation.persistence.DummyQueries;

public class DummyHandlers {

    public static final int DEFINE_WITH = 0;
    public static final int CHANGE_NAME = 1;
    public static final int QUERY_ALL = 2;
    public static final int ADAPT_STATE = 3;
    public static final HandlerEntry<Three<Completes<DummyState>, Stage, DummyData>> defineWithHandler =
           HandlerEntry.of(DEFINE_WITH, (stage, dummyData) -> Dummy.defineWith(stage, dummyData.name));

    public static final HandlerEntry<Three<Completes<DummyState>, Dummy, DummyData>> changeNameHandler =
            HandlerEntry.of(CHANGE_NAME, (dummy, dummyData) -> dummy.withName(dummyData.name));

    public static final HandlerEntry<Two<Completes<DummyData>, DummyQueries>> queryAllHandler =
            HandlerEntry.of(QUERY_ALL, DummyQueries::allDummies);

    public static final HandlerEntry<Two<DummyData, DummyState>> adaptStateHandler =
            HandlerEntry.of(ADAPT_STATE, DummyData::from);


}
