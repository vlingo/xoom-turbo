// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.stepflow;

import io.vlingo.xoom.common.Completes;

/**
 * A {@link StateHandler} is a functional interface that describes a {@link StateTransition}.
 *
 * @param <T> is the source {@link State}
 * @param <R> is the target {@link State}
 */
@SuppressWarnings("rawtypes")
public interface StateHandler<T extends State, R extends State> {
    Completes<StateTransition<T, R, ?>> execute();
}
