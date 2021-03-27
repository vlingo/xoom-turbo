// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.exchange;

import io.vlingo.actors.Grid;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.dispatch.NoOpDispatcher;

public interface ExchangeInitializer {

  void init(final Grid grid);

  default Dispatcher dispatcher() {
    return new NoOpDispatcher();
  }

}
