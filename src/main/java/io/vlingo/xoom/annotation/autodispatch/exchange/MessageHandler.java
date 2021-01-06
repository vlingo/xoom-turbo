// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.autodispatch.exchange;

import io.vlingo.actors.Stage;

public interface MessageHandler {

    @FunctionalInterface
    interface Basic<A, B> extends MessageHandler {
        void handle(A a, B b);
    }

    @FunctionalInterface
    interface WithStage<A> extends MessageHandler {
        void handle(final Stage stage, A a);
    }

}
