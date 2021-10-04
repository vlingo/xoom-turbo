// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ComponentRegistryTest {

  @Before
  @After
  public void clear() {
    ComponentRegistry.clear();
  }

  @Test
  public void testThatComponentsAreRegistered() {
    ComponentRegistry.register("appName", "xoom-app");
    ComponentRegistry.register(Boolean.class, Boolean.TRUE);
    Assert.assertTrue(ComponentRegistry.has("appName"));
    Assert.assertTrue(ComponentRegistry.has(Boolean.class));
    Assert.assertFalse(ComponentRegistry.has("component"));
    Assert.assertFalse(ComponentRegistry.has(Integer.class));
    Assert.assertEquals("xoom-app", ComponentRegistry.withName("appName"));
    Assert.assertEquals(Boolean.TRUE, ComponentRegistry.withType(Boolean.class));
  }

  @Test
  public void testThatComponentsAreUnregistered() {
    ComponentRegistry.register("appName", "xoom-app");
    ComponentRegistry.register(Boolean.class, Boolean.TRUE);

    Assert.assertTrue(ComponentRegistry.has("appName"));
    Assert.assertTrue(ComponentRegistry.has(Boolean.class));

    ComponentRegistry.unregister("appName");
    ComponentRegistry.unregister(Boolean.class);

    Assert.assertFalse(ComponentRegistry.has("appName"));
    Assert.assertFalse(ComponentRegistry.has(Boolean.class));
  }

}
