// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer;

public class XoomInitializerStatements {

    public static final String WORLD_INSTANCE_STATEMENT = "world = $T.startWithDefaults($S)";
    public static final String BASIC_STAGE_INSTANCE_STATEMENT = "final $T stage = world.stageNamed($S)";
    public static final String STAGE_INSTANCE_STATEMENT = "final $T stage = world.stageNamed($S, $T.class, new $T($T.$L))";
    public static final String SERVER_INSTANCE_STATEMENT = "server = $T.startWith(stage, $T.are(), port, $T.define(), $T.define())";

    public static final String INITIALIZER_INSTANCE_STATEMENT = "System.out.println(\"=========================\");\n" +
            "System.out.println(\"service: $L.\");\n" +
            "System.out.println(\"=========================\");\n" +
            "instance = new XoomInitializer(resolvePort(args));\n";

    public static final String SHUTDOWN_HOOK_STATEMENT = "Runtime.getRuntime().addShutdownHook(new Thread(() -> { \n" +
            " if (instance != null) { \n" +
            "instance.server.stop(); \n" +
            "System.out.println(\"=========================\");\n" +
            "System.out.println(\"Stopping $L.\");\n" +
            "System.out.println(\"=========================\");\n" +
            "} \n" +
            "}))\n";

}
