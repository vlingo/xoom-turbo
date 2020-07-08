// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.initializer.methods;

import com.squareup.javapoet.MethodSpec;
import io.vlingo.xoom.annotation.initializer.Xoom;

import javax.lang.model.element.Element;

import static io.vlingo.xoom.annotation.initializer.XoomInitializerStatements.INITIALIZER_INSTANCE_STATEMENT;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class MainMethod {

    public static MethodSpec from(final Element bootstrapClass) {
        final Xoom xoomAnnotation =
                bootstrapClass.getAnnotation(Xoom.class);

        return MethodSpec.methodBuilder("main")
                .addModifiers(PUBLIC, STATIC).returns(void.class)
                .addParameter(String[].class, "args").addException(Exception.class)
                .addStatement(INITIALIZER_INSTANCE_STATEMENT, xoomAnnotation.name())
                .build();
    }

}
