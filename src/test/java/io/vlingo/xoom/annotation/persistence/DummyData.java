// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.persistence;

import io.vlingo.xoom.annotation.model.DummyState;

public class DummyData {

    public final String name;

    public static DummyData from(DummyState state) {
        return new DummyData(state.name);
    }

    public DummyData(String name) {
        this.name = name;
    }
}
