// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import io.vlingo.xoom.annotation.initializer.Xoom;

import javax.lang.model.element.Element;

import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class PortResolverMethod {

    public static MethodSpec from(final Element bootstrapClass) {
        final Xoom xoomAnnotation =
                bootstrapClass.getAnnotation(Xoom.class);

        return MethodSpec.methodBuilder("resolvePort")
                .addModifiers(PUBLIC, STATIC).returns(TypeName.INT)
                .addParameter(String[].class, "args")
                .beginControlFlow("try")
                .addStatement("return Integer.parseInt(args[0])")
                .nextControlFlow("catch (final $T e)", Exception.class)
                .addStatement("System.out.println(\"$L: Command line does not provide a valid port; defaulting to: \" + DEFAULT_PORT)", xoomAnnotation.name())
                .addStatement("return DEFAULT_PORT")
                .endControlFlow()
                .build();
    }

}
