// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom;

import io.vlingo.actors.Actor;

public class TestRequestProtocolActor extends Actor implements TestRequestProtocol {

    public TestRequestProtocolActor() {
        System.out.println("request actor");
    }

    @Override
    public void request(int value, TestResponseProtocol respondTo) {
        respondTo.response(value + 1, selfAs(TestRequestProtocol.class));
    }

}
