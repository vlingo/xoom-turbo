// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom;

import io.vlingo.actors.Actor;
import io.vlingo.actors.Stoppable;
import io.vlingo.common.Completes;
import io.vlingo.xoom.TestRequestProtocol.TestResponseProtocol;

public class TestResponseProtocolActor extends Actor implements TestResponseProtocol, Stoppable {
    private final TestRequestProtocol requester;
    private int total;

    public TestResponseProtocolActor(final TestRequestProtocol requester) {
        this.requester = requester;
        this.total = 0;
    }

    @Override
    public void start() {
        requester.request(total, selfAs(TestResponseProtocol.class));
    }

    @Override
    public void response(final int value, final TestRequestProtocol requestOf) {
        if (value >= 10) {
            total = value;
        } else {
            requestOf.request(value + 1, selfAs(TestResponseProtocol.class));
        }
    }

    @Override
    public Completes<Integer> total() {
        return completes().with(total);
    }
}