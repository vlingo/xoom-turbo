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

/**
 * Accessors for common infrastructure adapters.
 */
public interface CommonAdapters {

  /**
   * Answer the registered {@code Dispatcher} of {@code Dispatchable} type instances.
   * @return {@code Dispatcher<? extends Dispatchable>}
   */
  @SuppressWarnings("rawtypes")
  Dispatcher<? extends Dispatchable> dispatchableDispatcher();

  /**
   * Answer the registered {@code Journal<T>} where {@code T} is a parameter type.
   * @param <T> the type that the Journal persists
   * @return {@code Journal<T>}
   */
  <T> Journal<T> journal();

  /**
   * Answer the registered {@code ProjectionDispatcher}.
   * @return ProjectionDispatcher
   */
  ProjectionDispatcher projectionDispatcher();

  /**
   * Answer the registered {@code StateStore} for the CQRS query/read model.
   * @return ProjectionDispatcher
   */
  StateStore queryModelStateStore();

  /**
   * Answer the registered XOOM HTTP {@code Server}.
   * @return Server
   */
  Server server();
}
