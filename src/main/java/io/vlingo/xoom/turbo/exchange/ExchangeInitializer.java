// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.exchange;

import io.vlingo.xoom.lattice.grid.Grid;
import io.vlingo.xoom.symbio.store.dispatch.Dispatcher;
import io.vlingo.xoom.symbio.store.dispatch.NoOpDispatcher;

public interface ExchangeInitializer {

  void init(final Grid grid);

  @SuppressWarnings("rawtypes")
  default Dispatcher dispatcher() {
    return new NoOpDispatcher();
  }
}
