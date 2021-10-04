// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public class ComponentRegistry {

  private static final Map<String, Object> components = new HashMap<>();

  public static void register(final Class<?> componentClass, final Object componentInstance) {
    components.put(componentClass.getCanonicalName(), componentInstance);
  }

  public static void register(final String componentName, final Object componentInstance) {
    components.put(componentName, componentInstance);
  }

  public static <T> T withType(final Class<T> componentClass) {
    return withName(componentClass.getCanonicalName());
  }

  public static <T> T withName(final String name) {
    return (T) components.get(name);
  }

  public static boolean has(final Class<?> componentClass) {
    return has(componentClass.getCanonicalName());
  }

  public static boolean has(final String componentName) {
    return components.containsKey(componentName);
  }

  public static void unregister(final String ...componentNames) {
    Stream.of(componentNames).forEach(components::remove);
  }

  public static void unregister(final Class<?> ...componentClasses) {
    Stream.of(componentClasses).map(Class::getCanonicalName).forEach(ComponentRegistry::unregister);
  }

  public static void clear() {
    components.clear();
  }

}
