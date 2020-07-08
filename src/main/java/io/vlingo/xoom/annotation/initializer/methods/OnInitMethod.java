// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer.methods;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import io.vlingo.actors.Stage;
import io.vlingo.xoom.annotation.initializer.Xoom;

import javax.lang.model.element.Element;

import static javax.lang.model.element.Modifier.PUBLIC;

public class OnInitMethod {

    public static MethodSpec from(final Element bootstrapClass) {
        final Xoom xoomAnnotation =
                bootstrapClass.getAnnotation(Xoom.class);

        return MethodSpec.methodBuilder("onInit")
                .addModifiers(PUBLIC).returns(TypeName.VOID)
                .addParameter(Stage.class, "stage")
                .build();
    }

}
