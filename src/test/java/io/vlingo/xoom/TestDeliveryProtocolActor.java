// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom;

import io.vlingo.actors.Actor;
import io.vlingo.common.Completes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestDeliveryProtocolActor extends Actor implements TestDeliveryProtocol {

    private final List<String> reactions;

    public TestDeliveryProtocolActor() {
        this.reactions = new ArrayList<>();
    }

    @Override
    public void reactTo() {
        final String reaction = "reacting to no parameters";
        logger().debug(reaction);
        reactions.add(reaction);
    }

    @Override
    public void reactTo(final int x, final int y, final int z) {
        final String reaction = "reacting to: x=" + x + " y=" + y + " z=" + z;
        logger().debug(reaction);
        reactions.add(reaction);
    }

    @Override
    public void reactTo(final String text) {
        final String reaction = "reacting to: text=" + text;
        logger().debug(reaction);
        reactions.add(reaction);
    }

    @Override
    public Completes<List<String>> reactions() {
        logger().debug("reactions...");
        return completes().with(Collections.unmodifiableList(reactions));
    }

}
