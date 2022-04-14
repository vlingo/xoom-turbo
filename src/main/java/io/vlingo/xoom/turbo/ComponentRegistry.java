// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
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

  private final Map<String, Object> components = new HashMap<>();
  private static final ComponentRegistry instance = new ComponentRegistry();

  public static ComponentRegistry register(final Class<?> componentClass, final Object componentInstance) {
    instance.components.put(componentClass.getCanonicalName(), componentInstance);
    return instance;
  }

  public static ComponentRegistry register(final String componentName, final Object componentInstance) {
    instance.components.put(componentName, componentInstance);
    return instance;
  }

  public static <T> T withType(final Class<T> componentClass) {
    return withName(componentClass.getCanonicalName());
  }

  public static <T> T withName(final String name) {
    return (T) instance.components.get(name);
  }

  public static boolean has(final Class<?> componentClass) {
    return has(componentClass.getCanonicalName());
  }

  public static boolean has(final String componentName) {
    return instance.components.containsKey(componentName);
  }

  public static void unregister(final String... componentNames) {
    Stream.of(componentNames).forEach(instance.components::remove);
  }

  public static void unregister(final Class<?>... componentClasses) {
    Stream.of(componentClasses).map(Class::getCanonicalName).forEach(ComponentRegistry::unregister);
  }

  public static void clear() {
    instance.components.clear();
  }
}

