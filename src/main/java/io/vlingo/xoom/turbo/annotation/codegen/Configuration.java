// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.annotation.codegen;

import static io.vlingo.xoom.turbo.annotation.codegen.Template.ENTRY_ADAPTER;
import static io.vlingo.xoom.turbo.annotation.codegen.Template.JOURNAL_PROVIDER;
import static io.vlingo.xoom.turbo.annotation.codegen.Template.STATE_ADAPTER;
import static io.vlingo.xoom.turbo.annotation.codegen.Template.STATE_STORE_PROVIDER;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.vlingo.xoom.turbo.annotation.codegen.storage.StorageType;

public class Configuration {

  public static final Map<StorageType, String> ADAPTER_TEMPLATES =
          Collections.unmodifiableMap(
                  new HashMap<StorageType, String>() {
                    private static final long serialVersionUID = -5145323372016918495L;

                  {
                    put(StorageType.STATE_STORE, STATE_ADAPTER.filename);
                    put(StorageType.JOURNAL, ENTRY_ADAPTER.filename);
                  }}
          );

  public static final Map<StorageType, String> COMMAND_MODEL_STORE_TEMPLATES =
          Collections.unmodifiableMap(
                  new HashMap<StorageType, String>() {
                    private static final long serialVersionUID = 8463765953835505703L;

                  {
                    put(StorageType.STATE_STORE, STATE_STORE_PROVIDER.filename);
                    put(StorageType.JOURNAL, JOURNAL_PROVIDER.filename);
                  }}
          );

  @SuppressWarnings("serial")
  public static final Map<StorageType, String> QUERY_MODEL_STORE_TEMPLATES =
          Collections.unmodifiableMap(
                  new HashMap<StorageType, String>() {{
                    put(StorageType.STATE_STORE, STATE_STORE_PROVIDER.filename);
                    put(StorageType.JOURNAL, STATE_STORE_PROVIDER.filename);
                  }}
          );
}
