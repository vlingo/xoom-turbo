// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer;

import io.vlingo.xoom.annotation.AnnotatedElements;
import io.vlingo.xoom.annotation.ProcessingAnnotationException;
import io.vlingo.xoom.annotation.initializer.contentloader.CodeGenerationContextLoader;
import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.CodeGenerationException;
import io.vlingo.xoom.codegen.content.ContentCreationStep;
import io.vlingo.xoom.codegen.template.autodispatch.AutoDispatchResourceHandlerGenerationStep;
import io.vlingo.xoom.codegen.template.bootstrap.BootstrapGenerationStep;
import io.vlingo.xoom.codegen.template.projections.ProjectionGenerationStep;
import io.vlingo.xoom.codegen.template.storage.StorageGenerationStep;

import javax.annotation.processing.ProcessingEnvironment;
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
                             final AnnotatedElements annotatedElements) {
        try {
            final String basePackage =
                    XoomInitializerPackage.from(environment, annotatedElements);

            final CodeGenerationContext context =
                    CodeGenerationContextLoader.from(environment.getFiler(), basePackage,
                            annotatedElements, environment);

            Stream.of(new ProjectionGenerationStep(), new AutoDispatchResourceHandlerGenerationStep(),
                    new StorageGenerationStep(), new BootstrapGenerationStep(), new ContentCreationStep())
                    .filter(step -> step.shouldProcess(context)).forEach(step -> step.process(context));
        } catch (final CodeGenerationException exception) {
            throw new ProcessingAnnotationException(exception);
        }
    }

}
