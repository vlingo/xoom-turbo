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
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;

import static io.vlingo.xoom.annotation.initializer.XoomInitializerGenerator.XOOM_INITIALIZER_CLASS_NAME;
import static io.vlingo.xoom.annotation.initializer.XoomInitializerStatements.INITIALIZER_ASSIGNMENT_STATEMENT;
import static io.vlingo.xoom.annotation.initializer.XoomInitializerStatements.INITIALIZER_INSTANTIATION_STATEMENT;

public class InitializationAwareStatementResolver {

    public static Entry<String, Object[]> resolve(final String basePackage,
                                                      final Elements elements,
                                                      final TypeElement bootstrapClass) {
        final ClassName generatedInitializer =
                ClassName.get(basePackage, XOOM_INITIALIZER_CLASS_NAME);

        final TypeMirror stageInitializerAwareInterface =
                elements.getTypeElement(XoomInitializationAware.class.getCanonicalName()).asType();

        if(bootstrapClass.getInterfaces().contains(stageInitializerAwareInterface)) {
            return new SimpleEntry<>(INITIALIZER_INSTANTIATION_STATEMENT,
                    new Object[]{bootstrapClass, bootstrapClass});
        }

        return new SimpleEntry<>(INITIALIZER_ASSIGNMENT_STATEMENT, new Object[]{generatedInitializer});
    }
}
