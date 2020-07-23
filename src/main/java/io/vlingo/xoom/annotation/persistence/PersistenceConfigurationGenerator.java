// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.persistence;

import io.vlingo.xoom.annotation.ProcessingAnnotationException;
import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.CodeGenerationException;
import io.vlingo.xoom.codegen.content.ContentCreationStep;
import io.vlingo.xoom.codegen.template.projections.ProjectionGenerationStep;
import io.vlingo.xoom.codegen.template.storage.StorageGenerationStep;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.util.Set;
import java.util.stream.Stream;

public class PersistenceConfigurationGenerator {

    private static PersistenceConfigurationGenerator instance;

    private PersistenceConfigurationGenerator() { }

    public static PersistenceConfigurationGenerator instance() {
        if(instance == null) {
            instance = new PersistenceConfigurationGenerator();
        }
        return instance;
    }

    public void generateFrom(final ProcessingEnvironment environment,
                             final Set<? extends Element> annotatedElements) {
        try {
            final Element annotatedClass =
                    annotatedElements.stream().findFirst().get();

            final CodeGenerationContext context =
                    CodeGenerationContextLoader.from(environment, annotatedClass);

            Stream.of(new ProjectionGenerationStep(), new StorageGenerationStep(), new ContentCreationStep())
                    .filter(step -> step.shouldProcess(context))
                    .forEach(step -> step.process(context));
        } catch (final CodeGenerationException exception) {
            throw new ProcessingAnnotationException(exception);
        }
    }

}
