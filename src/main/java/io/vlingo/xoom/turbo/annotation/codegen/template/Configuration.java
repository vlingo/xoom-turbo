// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.annotation.codegen.template;

import io.vlingo.xoom.turbo.annotation.codegen.template.storage.StorageType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static io.vlingo.xoom.turbo.annotation.codegen.template.Template.*;

public class Configuration {

  public static final Map<StorageType, String> ADAPTER_TEMPLATES =
          Collections.unmodifiableMap(
                  new HashMap<StorageType, String>() {{
                    put(StorageType.STATE_STORE, STATE_ADAPTER.filename);
                    put(StorageType.JOURNAL, ENTRY_ADAPTER.filename);
                  }}
          );
  
  public static final Map<StorageType, String> COMMAND_MODEL_STORE_TEMPLATES =
          Collections.unmodifiableMap(
                  new HashMap<StorageType, String>() {{
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
