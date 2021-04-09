// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.autodispatch;

public interface Handler {

    @FunctionalInterface
    interface Two<A, B> extends Handler {
        A handle(B b);
    }

    @FunctionalInterface
    interface Three<A, B, C> extends Handler {
        A handle(B b, C c);
    }

    @FunctionalInterface
    interface Four<A, B, C, D> extends Handler {
        A handle(B b, C c, D d);
    }

    @FunctionalInterface
    interface Five<A, B, C, D, E> extends Handler {
        A handle(B b, C c, D d, E e);
    }

}
