// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import static io.vlingo.xoom.annotation.initializer.XoomInitializerGenerator.XOOM_INITIALIZER_CLASS_NAME;

public class XoomInitializerType {

    public static TypeSpec from(final List<FieldSpec> fieldSpecs,
                                final List<MethodSpec> methodSpecs) {
        return TypeSpec.classBuilder(XOOM_INITIALIZER_CLASS_NAME)
                .addFields(fieldSpecs).addMethods(methodSpecs).build();
    }

}
