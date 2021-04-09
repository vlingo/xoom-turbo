// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.annotation.model;

import io.vlingo.xoom.symbio.store.object.StateObject;

public class OtherDummyState extends StateObject {

    public final String name;

    public OtherDummyState(final String name) {
        this.name = name;
    }
}
