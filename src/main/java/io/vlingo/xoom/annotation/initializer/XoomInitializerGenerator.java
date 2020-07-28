// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer;

import io.vlingo.xoom.annotation.ProcessingAnnotationException;
import io.vlingo.xoom.annotation.persistence.Persistence;
import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.CodeGenerationException;
import io.vlingo.xoom.codegen.content.ContentCreationStep;
import io.vlingo.xoom.codegen.template.bootstrap.BootstrapGenerationStep;
import io.vlingo.xoom.codegen.template.projections.ProjectionGenerationStep;
import io.vlingo.xoom.codegen.template.storage.StorageGenerationStep;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

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
                             final Map<Class, Set<Element>> annotatedElements) {

        try {
            final Element bootstrapClass =
                    annotatedElements.get(Xoom.class)
                            .stream().findFirst().get();

            final Element persistenceSetupClass =
                    annotatedElements.get(Persistence.class)
                            .stream().findFirst().orElse(null);

            final String basePackage =
                    XoomInitializerPackage.from(environment, bootstrapClass);

            final CodeGenerationContext context =
                    CodeGenerationContextLoader.from(basePackage, bootstrapClass,
                            persistenceSetupClass, environment);

            Stream.of(new ProjectionGenerationStep(), new StorageGenerationStep(),
                    new BootstrapGenerationStep(), new ContentCreationStep())
                    .filter(step -> step.shouldProcess(context))
                    .forEach(step -> step.process(context));
        } catch (final CodeGenerationException exception) {
            throw new ProcessingAnnotationException(exception);
        }
    }

}
