// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import io.vlingo.actors.Stage;
import io.vlingo.common.identity.IdentityGeneratorType;
import io.vlingo.http.resource.Configuration.Sizing;
import io.vlingo.http.resource.Configuration.Timing;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import static io.vlingo.xoom.annotation.initializer.XoomInitializerGenerator.XOOM_INITIALIZER_CLASS_NAME;
import static io.vlingo.xoom.annotation.initializer.XoomInitializerStatements.*;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class XoomInitializerMethods {

    private final Xoom xoomAnnotation;

    public static List<MethodSpec> from(final Xoom xoomAnnotation) {
        final XoomInitializerMethods methods =
                new XoomInitializerMethods(xoomAnnotation);

        return Arrays.asList(methods.mainMethod(),
                methods.resolvePortMethod(),
                methods.constructorMethod());
    }

    private XoomInitializerMethods(final Xoom xoomAnnotation) {
        this.xoomAnnotation = xoomAnnotation;
    }

    private MethodSpec mainMethod() {
        return MethodSpec.methodBuilder("main")
                .addModifiers(PUBLIC, STATIC).returns(void.class).addParameter(String[].class, "args")
                .addStatement(INITIALIZER_INSTANCE_STATEMENT, xoomAnnotation.name())
                .build();
    }

    private MethodSpec resolvePortMethod() {
        return MethodSpec.methodBuilder("main")
                .addModifiers(PUBLIC, STATIC).returns(TypeName.INT)
                .addParameter(String[].class, "args")
                .beginControlFlow("try")
                .addStatement("return Integer.parseInt(args[0]);")
                .endControlFlow(PORT_EXCEPTION_HANDLING, xoomAnnotation.name())
                .build();
    }

    private MethodSpec constructorMethod() {
        final Entry<String, Object[]> stageInstanceStatement = resolveStageInstanceStatement();
        return MethodSpec.methodBuilder(XOOM_INITIALIZER_CLASS_NAME)
                .addModifiers(PUBLIC).addParameter(Integer.class, "port")
                .addStatement(WORLD_INSTANCE_STATEMENT, xoomAnnotation.name())
                .addStatement(stageInstanceStatement.getKey(), stageInstanceStatement.getValue())
                .addStatement(SERVER_INSTANCE_STATEMENT, Timing.class, Sizing.class)
                .addStatement(SHUTDOWN_HOOK_STATEMENT, xoomAnnotation.name())
                .build();
    }

    private Entry<String, Object[]> resolveStageInstanceStatement() {
        if(xoomAnnotation.addressFactory().isBasic()) {
            return new SimpleEntry(BASIC_STAGE_INSTANCE_STATEMENT,
                    new Object[]{xoomAnnotation.name()});
        }

        return new SimpleEntry(STAGE_INSTANCE_STATEMENT,
                new Object[]{xoomAnnotation.name(), Stage.class,
                        xoomAnnotation.addressFactory().clazz,
                        IdentityGeneratorType.class});
    }

}
