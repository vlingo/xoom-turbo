// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo;

public class EnvironmentVariables {

  public static String retrieve(final String key) {
    if(!ComponentRegistry.has(EnvironmentVariablesRetriever.class)) {
      ComponentRegistry.register(EnvironmentVariablesRetriever.class, new DefaultRetriever());
    }
    return ComponentRegistry.withType(EnvironmentVariablesRetriever.class).retrieve(key);
  }

  public static boolean containsKey(final String key) {
    if(!ComponentRegistry.has(EnvironmentVariablesRetriever.class)) {
      ComponentRegistry.register(EnvironmentVariablesRetriever.class, new DefaultRetriever());
    }
    return ComponentRegistry.withType(EnvironmentVariablesRetriever.class).containsKey(key);
  }

  public interface EnvironmentVariablesRetriever {
    String retrieve(final String key);
    boolean containsKey(final String key);
  }

  private static class DefaultRetriever implements EnvironmentVariablesRetriever {
    @Override
    public String retrieve(String key) {
      return System.getenv(key);
    }

    @Override
    public boolean containsKey(String key) {
      return System.getenv().containsKey(key);
    }
  }
}
