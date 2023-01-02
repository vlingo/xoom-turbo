// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.codegen;

public enum Template {

  JOURNAL_PROVIDER("JournalProvider"),
  STATE_STORE_PROVIDER("StateStoreProvider"),
  ENTRY_ADAPTER("EntryAdapter"),
  STATE_ADAPTER("StateAdapter"),
  REST_RESOURCE("RestResource"),
  XOOM_INITIALIZER("XoomInitializer"),
  PROJECTION_DISPATCHER_PROVIDER("ProjectionDispatcherProvider"),
  REST_RESOURCE_CREATION_METHOD("RestResourceCreationMethod"),
  REST_RESOURCE_RETRIEVE_METHOD("RestResourceRetrieveMethod"),
  REST_RESOURCE_UPDATE_METHOD("RestResourceUpdateMethod");

  public final String filename;

  Template(final String filename) {
    this.filename = filename;
  }

}
