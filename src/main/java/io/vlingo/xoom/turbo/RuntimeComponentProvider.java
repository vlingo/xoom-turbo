// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo;

import io.vlingo.xoom.http.resource.Server;
import io.vlingo.xoom.lattice.model.projection.ProjectionDispatcher;
import io.vlingo.xoom.symbio.store.dispatch.Dispatchable;
import io.vlingo.xoom.symbio.store.dispatch.Dispatcher;
import io.vlingo.xoom.symbio.store.journal.Journal;
import io.vlingo.xoom.symbio.store.state.StateStore;

public interface RuntimeComponentProvider {

  @SuppressWarnings("rawtypes")
  Dispatcher<? extends Dispatchable> dispatchableDispatcher();

  Journal<String> journal();

  ProjectionDispatcher projectionDispatcher();

  StateStore queryModelStateStore();

  Server server();
}
