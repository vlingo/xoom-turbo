// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo;

import io.vlingo.xoom.turbo.EnvironmentVariables.EnvironmentVariablesRetriever;

import java.util.HashMap;
import java.util.Map;

public class MockEnvironmentVariables implements EnvironmentVariablesRetriever {

  private final Map<String, String> values = new HashMap<>();

  public MockEnvironmentVariables(final Map<String, String> values) {
    this.values.putAll(values);
  }

  @Override
  public String retrieve(final String key) {
    return values.get(key);
  }

  @Override
  public boolean containsKey(final String key) {
    return values.containsKey(key);
  }

}
