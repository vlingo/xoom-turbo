// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.initializer.methods;

import com.squareup.javapoet.ClassName;
import io.vlingo.xoom.XoomInitializationAware;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import static io.vlingo.xoom.annotation.initializer.XoomInitializerGenerator.XOOM_INITIALIZER_CLASS_NAME;

public class InitializationAwareClassResolver {

    public static Object[] resolve(final String basePackage,
                            final Elements elements,
                            final TypeElement bootstrapClass) {
        final TypeMirror stageInitializerAwareInterface =
                elements.getTypeElement(XoomInitializationAware.class.getCanonicalName()).asType();

        final Object initializerClass =
                bootstrapClass.getInterfaces().contains(stageInitializerAwareInterface) ?
                        bootstrapClass : ClassName.get(basePackage, XOOM_INITIALIZER_CLASS_NAME);

        return new Object[]{initializerClass, initializerClass};
    }
}
