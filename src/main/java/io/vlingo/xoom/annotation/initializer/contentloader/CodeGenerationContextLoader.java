// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer.contentloader;

import io.vlingo.xoom.annotation.AnnotatedElements;
import io.vlingo.xoom.annotation.autodispatch.AutoDispatchGenerationParameter;
import io.vlingo.xoom.annotation.autodispatch.Model;
import io.vlingo.xoom.annotation.autodispatch.Queries;
import io.vlingo.xoom.annotation.initializer.CodeGenerationParameterResolver;
import io.vlingo.xoom.annotation.initializer.Xoom;
import io.vlingo.xoom.annotation.persistence.Persistence;
import io.vlingo.xoom.codegen.CodeGenerationContext;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.*;

public class CodeGenerationContextLoader {

    private final Filer filer;
    private final String basePackage;
    private final ProcessingEnvironment environment;
    private final TypeElement bootstrapClass;
    private final TypeElement persistenceSetupClass;
    private final Set<TypeElement> autoDispatchResourceClasses = new HashSet<>();

    public static CodeGenerationContext from(final Filer filer,
                                             final String basePackage,
                                             final AnnotatedElements elements,
                                             final ProcessingEnvironment environment) {
        return new CodeGenerationContextLoader(filer, basePackage,
                elements, environment).load();
    }

    public CodeGenerationContextLoader(final Filer filer,
                                       final String basePackage,
                                       final AnnotatedElements elements,
                                       final ProcessingEnvironment environment) {
        this.filer = filer;
        this.environment = environment;
        this.basePackage = basePackage;
        this.bootstrapClass = elements.elementWith(Xoom.class);
        this.persistenceSetupClass = elements.elementWith(Persistence.class);
        this.autoDispatchResourceClasses.addAll(elements.elementsWith(Model.class, Queries.class));
    }

    public CodeGenerationContext load() {
        final CodeGenerationParameterResolver parameterResolver =
                CodeGenerationParameterResolver.from(basePackage, bootstrapClass,
                        persistenceSetupClass, autoDispatchResourceClasses,
                        environment);

        return CodeGenerationContext.using(filer, bootstrapClass)
                .with(resolveContentLoaders())
                .on(parameterResolver.resolve())
                .on(new AutoDispatchGenerationParameter().resolve());
    }

    private List<ContentLoader> resolveContentLoaders() {
        if(bootstrapClass == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(new ProjectionActorContentLoader(persistenceSetupClass, environment),
                new StateContentLoader(persistenceSetupClass, environment),
                new QueriesContentLoader(persistenceSetupClass, environment),
                new RestResourceContentLoader(bootstrapClass, environment));
    }

}