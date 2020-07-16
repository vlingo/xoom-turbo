// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.persistence;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import io.vlingo.xoom.annotation.ProcessingAnnotationException;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.io.IOException;
import java.util.Set;

public class PersistenceConfigurationGenerator {

    private static PersistenceConfigurationGenerator instance;

    private PersistenceConfigurationGenerator() { }

    public void generateFrom(final ProcessingEnvironment environment,
                             final Set<? extends Element> annotatedElements) {
        try {
            final Element annotatedClass =
                    annotatedElements.stream().findFirst().get();

            final String basePackage =
                    PersistenceConfigurationPackage.from(environment, annotatedClass);

            final TypeSpec typeSpec =
                    PersistenceConfigurationType.from(environment,
                            basePackage, annotatedClass);

            JavaFile.builder(basePackage, typeSpec)
                    .build().writeTo(environment.getFiler());
        } catch (final IOException exception) {
            throw new ProcessingAnnotationException(exception);
        }
    }


}
