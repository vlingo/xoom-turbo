// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.initializer;

import io.vlingo.xoom.turbo.annotation.AnnotatedElements;
import io.vlingo.xoom.turbo.annotation.ProcessingAnnotationException;
import io.vlingo.xoom.turbo.annotation.initializer.contentloader.CodeGenerationContextLoader;
import io.vlingo.xoom.turbo.codegen.CodeGenerationContext;
import io.vlingo.xoom.turbo.codegen.CodeGenerationException;
import io.vlingo.xoom.turbo.codegen.content.ContentCreationStep;
import io.vlingo.xoom.turbo.codegen.template.autodispatch.AutoDispatchResourceHandlerGenerationStep;
import io.vlingo.xoom.turbo.codegen.template.bootstrap.BootstrapGenerationStep;
import io.vlingo.xoom.turbo.codegen.template.projections.ProjectionGenerationStep;
import io.vlingo.xoom.turbo.codegen.template.storage.StorageGenerationStep;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.stream.Stream;

public class XoomInitializerGenerator {

    private static XoomInitializerGenerator instance;

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

            Stream.of(new ProjectionGenerationStep(), new StorageGenerationStep(),
                    new AutoDispatchResourceHandlerGenerationStep(),
                    new BootstrapGenerationStep(), new ContentCreationStep())
                    .filter(step -> step.shouldProcess(context)).forEach(step -> step.process(context));
        } catch (final CodeGenerationException exception) {
            throw new ProcessingAnnotationException(exception);
        }
    }

}
