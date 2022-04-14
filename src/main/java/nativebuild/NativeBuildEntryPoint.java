// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package nativebuild;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.lattice.exchange.ConnectionSettings;
import io.vlingo.xoom.turbo.annotation.PackageCollector;
import io.vlingo.xoom.turbo.exchange.ExchangeSettings;

public final class NativeBuildEntryPoint {
  @SuppressWarnings("unused")
  @CEntryPoint(name = "Java_io_vlingo_xoom_turbonative_Native_start")
  public static int start(@CEntryPoint.IsolateThreadContext long isolateId, CCharPointer name) {
    final String nameString = CTypeConversion.toJavaString(name);
    World world = World.startWithDefaults(nameString);

    final Path projectPath =
        Paths.get(System.getProperty("user.dir"));
    final Set<String> packages =
        PackageCollector.from(projectPath, "io.vlingo.xoom.turbo.annotation").collectAll();
    final ConnectionSettings firstSettings =
        ExchangeSettings.of("first").mapToConnection();
    return 0;
  }
}
