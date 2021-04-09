// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class PackageCollectorTest {

  @Test
  public void testThatPackagesAreCollected() {
    final Path projectPath =
            Paths.get(System.getProperty("user.dir"));

    final Set<String> packages =
            PackageCollector.from(projectPath, "io.vlingo.xoom.turbo.annotation").collectAll();

    Assert.assertEquals(5, packages.size());
    Assert.assertTrue(packages.contains("io.vlingo.xoom.turbo.annotation"));
    Assert.assertTrue(packages.contains("io.vlingo.xoom.turbo.annotation.persistence"));
    Assert.assertTrue(packages.contains("io.vlingo.xoom.turbo.annotation.autodispatch"));
    Assert.assertTrue(packages.contains("io.vlingo.xoom.turbo.annotation.initializer"));
    Assert.assertTrue(packages.contains("io.vlingo.xoom.turbo.annotation.initializer.contentloader"));
  }

}
