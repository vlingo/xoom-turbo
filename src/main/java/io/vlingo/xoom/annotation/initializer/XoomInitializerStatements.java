// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer;

public class XoomInitializerStatements {

    public static final String WORLD_INSTANCE_STATEMENT = "world = World.startWithDefaults(\"$S\");";
    public static final String BASIC_STAGE_INSTANCE_STATEMENT = "final Stage stage = world.stageNamed(\"$S\");";
    public static final String STAGE_INSTANCE_STATEMENT = "final Stage stage = world.stageNamed(\"$S\", $T.class, new $T($T.RANDOM));";
    public static final String SERVER_INSTANCE_STATEMENT = "server = Server.startWith(stage, allResources, port, $T.define(), $T.define());";

    public static final String INITIALIZER_INSTANCE_STATEMENT = "System.out.println(\"=========================\");" +
            "System.out.println(\"service: $S.\");" +
            "System.out.println(\"=========================\");" +
            "instance = new Bootstrap(resolvePort(args));";

    public static final String SHUTDOWN_HOOK_STATEMENT = "Runtime.getRuntime().addShutdownHook(new Thread(() -> { " +
            " if (instance != null) { " +
            "instance.server.stop(); " +
            "System.out.println(\"\n\"); " +
            "System.out.println(\"=========================\");" +
            "System.out.println(\"Stopping $S.\");" +
            "System.out.println(\"=========================\");" +
            "} " +
            "}))";

    public static final String PORT_EXCEPTION_HANDLING = "catch (final Exception e) { " +
            " System.out.println(\"$S: Command line does not provide a valid port; defaulting to: \" + DEFAULT_PORT); " +
            " return DEFAULT_PORT; " +
            "}";
}
