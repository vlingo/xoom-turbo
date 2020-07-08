// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.initializer.methods;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;

import static io.vlingo.xoom.annotation.initializer.XoomInitializerGenerator.XOOM_INITIALIZER_CLASS_NAME;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class InstanceMethod {

    public static MethodSpec from(final String basePackage) {
        ClassName.get(basePackage, XOOM_INITIALIZER_CLASS_NAME);
        return MethodSpec.methodBuilder("instance").addModifiers(PUBLIC, STATIC)
                .returns(ClassName.get(basePackage, XOOM_INITIALIZER_CLASS_NAME))
                .addStatement("return instance").build();
    }
}
