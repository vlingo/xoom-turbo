// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import io.vlingo.xoom.annotation.ProcessingAnnotationException;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.io.IOException;
import java.util.Set;

public class XoomInitializerGenerator {

    private static XoomInitializerGenerator instance;

    public static final String XOOM_INITIALIZER_CLASS_NAME = "XoomInitializer";

    private XoomInitializerGenerator() {
    }

    public static XoomInitializerGenerator instance() {
        if(instance == null) {
            instance = new XoomInitializerGenerator();
        }
        return instance;
    }

    public void generateFrom(final ProcessingEnvironment environment,
                             final Set<? extends Element> annotatedElements) {
        try {
            final Element bootstrapClass =
                    annotatedElements.stream().findFirst().get();

            final Xoom xoomAnnotation =
                    bootstrapClass.getAnnotation(Xoom.class);

            final String basePackage =
                    XoomInitializerPackage.from(environment, bootstrapClass);

            final TypeSpec typeSpec =
                    XoomInitializerType.from(XoomInitializerFields.from(basePackage),
                            XoomInitializerMethods.from(xoomAnnotation));

            JavaFile.builder(basePackage, typeSpec)
                    .build().writeTo(environment.getFiler());
        } catch (final IOException exception) {
            throw new ProcessingAnnotationException(exception);
        }
    }

}
