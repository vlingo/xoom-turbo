// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.annotation.model;

import io.vlingo.xoom.actors.Address;
import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.common.Completes;

public interface Dummy {

    static Completes<DummyState> defineWith(Stage stage, String name) {
        final Address address =
                stage.world().addressFactory().uniquePrefixedWith("g-");

        final Dummy dummy =
                stage.actorFor(Dummy.class, Definition.has(DummyEntity.class,
                        Definition.parameters(address.idString())), address);

        return dummy.defineWith(name);
    }

    Completes<DummyState> defineWith(String name);

    Completes<DummyState> withName(String name);

}
