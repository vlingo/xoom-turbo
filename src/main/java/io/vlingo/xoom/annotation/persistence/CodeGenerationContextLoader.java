// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.persistence;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.CodeGenerationParameter;
import io.vlingo.xoom.codegen.content.TypeBasedContentLoader;
import io.vlingo.xoom.codegen.file.InternalTargetPathLocator;
import io.vlingo.xoom.codegen.template.projections.ProjectionType;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.vlingo.xoom.codegen.CodeGenerationParameter.*;
import static io.vlingo.xoom.codegen.template.storage.DatabaseType.IN_MEMORY;

public class CodeGenerationContextLoader {

    private final TypeElement annotatedClass;
    private final ProcessingEnvironment environment;

    public static CodeGenerationContext from(final ProcessingEnvironment environment,
                                             final Element annotatedClass) {
        return new CodeGenerationContextLoader((TypeElement) annotatedClass, environment).load();
    }

    private CodeGenerationContextLoader(final TypeElement annotatedClass,
                                        final ProcessingEnvironment environment) {
        this.annotatedClass = annotatedClass;
        this.environment = environment;
    }

    private CodeGenerationContext load() {
        final String targetFolder =
                InternalTargetPathLocator.find(environment);

        final List<TypeBasedContentLoader> contentLoaders =
                Arrays.asList(new AggregateProtocolTypeBasedContentLoader(annotatedClass, environment),
                        new DomainEventTypeBasedContentLoader(annotatedClass, environment),
                        new StateTypeBasedContentLoader(annotatedClass, environment));

        return CodeGenerationContext.generateInternallyTo(targetFolder)
                .with(contentLoaders).on(resolveParameters());
    }

    private Map<CodeGenerationParameter, String> resolveParameters() {
        final Persistence persistenceAnnotation =
                annotatedClass.getAnnotation(Persistence.class);

        return new HashMap<CodeGenerationParameter, String>() {{
            put(PROJECTIONS, resolveProjections());
            put(PACKAGE, persistenceAnnotation.basePackage());
            put(CQRS, String.valueOf(persistenceAnnotation.cqrs()));
            put(STORAGE_TYPE, persistenceAnnotation.storageType().name());
            put(DATABASE, IN_MEMORY.name());
            put(COMMAND_MODEL_DATABASE, IN_MEMORY.name());
            put(QUERY_MODEL_DATABASE, IN_MEMORY.name());
        }};
    }

    private String resolveProjections() {
        final Projections projections =
                annotatedClass.getAnnotation(Projections.class);

        if(projections == null) {
            return ProjectionType.NONE.name();
        }

        return projections.type().name();
    }

}

