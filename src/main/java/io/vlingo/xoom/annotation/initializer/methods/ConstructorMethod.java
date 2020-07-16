// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.initializer.methods;

import com.squareup.javapoet.MethodSpec;
import io.vlingo.actors.World;
import io.vlingo.http.resource.Configuration;
import io.vlingo.http.resource.Server;
import io.vlingo.xoom.actors.Settings;
import io.vlingo.xoom.annotation.initializer.Xoom;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.Map.Entry;

import static io.vlingo.xoom.annotation.initializer.XoomInitializerStatements.*;
import static javax.lang.model.element.Modifier.PUBLIC;

public class ConstructorMethod {

    private final Elements elements;
    private final String basePackage;
    private final Xoom xoomAnnotation;
    private final TypeElement bootstrapClass;

    private ConstructorMethod(final String basePackage,
                              final Elements elements,
                              final TypeElement bootstrapClass) {
        this.elements = elements;
        this.basePackage = basePackage;
        this.bootstrapClass = bootstrapClass;
        this.xoomAnnotation = bootstrapClass.getAnnotation(Xoom.class);
    }

    public static MethodSpec from(final String basePackage,
                                  final ProcessingEnvironment environment,
                                  final Element bootstrapClass) {
        return new ConstructorMethod(basePackage, environment.getElementUtils(),
                (TypeElement) bootstrapClass).build();
    }

    private MethodSpec build() {
        final Xoom xoomAnnotation =
                bootstrapClass.getAnnotation(Xoom.class);

        final String blockingMailboxStatement =
                xoomAnnotation.blocking() ? BLOCKING_MAILBOX_ENABLING_STATEMENT :
                        BLOCKING_MAILBOX_DISABLING_STATEMENT;

        final Entry<String, Object[]> resourcesStatement =
                ResourcesStatementResolver.resolve(elements, bootstrapClass);

        final Entry<String, Object[]> stageInstanceStatement =
                StageInstanceStatementResolver.resolve(xoomAnnotation);

        final Entry<String, Object[]> initializationStatement =
                InitializationAwareStatementeResolver.resolve(basePackage, elements, bootstrapClass);

        return MethodSpec.constructorBuilder()
                .addModifiers(PUBLIC).addParameter(String[].class, "args")
                .addStatement(blockingMailboxStatement, Settings.class)
                .addStatement(WORLD_INSTANCE_STATEMENT, World.class, xoomAnnotation.name(), Settings.class)
                .addStatement(stageInstanceStatement.getKey(), stageInstanceStatement.getValue())
                .addStatement(initializationStatement.getKey(), initializationStatement.getValue())
                .addStatement(ON_INIT_STATEMENT).addStatement(SERVER_CONFIGURATION_STATEMENT, Configuration.class)
                .addStatement(resourcesStatement.getKey(), resourcesStatement.getValue())
                .addStatement(SERVER_INSTANCE_STATEMENT, Server.class)
                .addStatement(SHUTDOWN_HOOK_STATEMENT, xoomAnnotation.name())
                .build();
    }

}