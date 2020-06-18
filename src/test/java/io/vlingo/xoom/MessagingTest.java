// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom;

import io.vlingo.actors.World;
import io.vlingo.xoom.TestRequestProtocol.TestResponseProtocol;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MessagingTest {

    private World world;

    @Test
    public void testThatActorMessagesDeliver() {
        final TestDeliveryProtocol test = world.actorFor(TestDeliveryProtocol.class, TestDeliveryProtocolActor.class);

        test.reactTo();
        test.reactTo(1, 2, 3);
        test.reactTo("Hello, World!");

        test.reactions().andThenConsume(reactions -> assertEquals(3, reactions.size()));
    }

    @Test
    public void testThatRequestResponseDelivers() {
        final TestRequestProtocol requestOf = world.actorFor(TestRequestProtocol.class, TestRequestProtocolActor.class);
        final TestResponseProtocol respondTo = world.actorFor(TestResponseProtocol.class, TestResponseProtocolActor.class, requestOf);

        respondTo.total().andThenConsume(value -> assertTrue(10 <= value));
    }

    @Before
    public void setUp() {
        world = Boot.start("test-messaging");
    }

    @After
    public void tearDown() {
        world.terminate();
    }

}
