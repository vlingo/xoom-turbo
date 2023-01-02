// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.annotation.model;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.model.stateful.StatefulEntity;

public class DummyEntity extends StatefulEntity<DummyState> implements Dummy {

    private DummyState state;

    public DummyEntity() {
        super();
    }

    public DummyEntity(String id) {
        super(id);
    }

    @Override
    public Completes<DummyState> defineWith(String name) {
        if (state == null) {
            return apply(new DummyState(id, name), () -> state);
        } else {
            return completes().with(state);
        }
    }

    @Override
    public Completes<DummyState> withName(String name) {
        return null;
    }

    @Override
    protected void state(final DummyState state) {
        this.state = state;
    }

    @Override
    protected Class<DummyState> stateType() {
        return DummyState.class;
    }

}
