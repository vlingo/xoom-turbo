// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.stepflow;

import io.vlingo.xoom.actors.Logger;

/**
 * A {@link Transition} is a base interface for a {@link StateTransition} and describes the identity of a source
 * state and a target state.
 */
@SuppressWarnings("rawtypes")
public interface Transition {

  String getSourceName();

  String getTargetName();

  static <T1 extends State, R1 extends State> void logResult(T1 s, R1 t) {
    Logger.basicLogger().info(s.getVersion() + ": [" + s.getName() + "] to [" + t.getName() + "]");
  }
}
