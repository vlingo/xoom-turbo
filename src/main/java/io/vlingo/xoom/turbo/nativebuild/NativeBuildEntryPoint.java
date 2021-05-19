package io.vlingo.xoom.turbo.nativebuild;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.lattice.exchange.ConnectionSettings;
import io.vlingo.xoom.turbo.annotation.PackageCollector;
import io.vlingo.xoom.turbo.exchange.ExchangeSettings;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public final class NativeBuildEntryPoint {
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
