// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.persistence;

import io.vlingo.xoom.annotation.TypeRetriever;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameters;
import io.vlingo.xoom.codegen.template.projections.ProjectionType;
import io.vlingo.xoom.codegen.template.storage.StorageType;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.*;

public class PersistenceParameterResolver {

    private final TypeRetriever typeRetriever;
    private final TypeElement persistenceSetupClass;

    public PersistenceParameterResolver(final TypeElement persistenceSetupClass,
                                        final ProcessingEnvironment environment) {
        this.persistenceSetupClass = persistenceSetupClass;
        this.typeRetriever = TypeRetriever.with(environment);
    }

    public static PersistenceParameterResolver from(final TypeElement persistenceSetupClass,
                                                    final ProcessingEnvironment environment) {
        return new PersistenceParameterResolver(persistenceSetupClass, environment);
    }

    public CodeGenerationParameters resolve() {
        return CodeGenerationParameters.from(STORAGE_TYPE, resolveStorageType())
                .add(PROJECTION_TYPE, resolveProjections())
                .add(PROJECTABLES, resolveProjectables())
                .add(CQRS, resolveCQRS());
    }

    private String resolveStorageType() {
        if(persistenceSetupClass == null) {
            return StorageType.NONE.name();
        }
        return persistenceSetupClass.getAnnotation(Persistence.class)
                .storageType().name();
    }

    private String resolveCQRS() {
        if(persistenceSetupClass == null) {
            return String.valueOf(false);
        }
        return String.valueOf(persistenceSetupClass
                .getAnnotation(Persistence.class).cqrs());
    }

    private String resolveProjections() {
        final Projections projections =
                persistenceSetupClass == null ? null :
                        persistenceSetupClass.getAnnotation(Projections.class);

        if(projections == null) {
            return ProjectionType.NONE.name();
        }

        return ProjectionType.CUSTOM.name();
    }

    private String resolveProjectables() {
        final Projections projections =
                persistenceSetupClass == null ? null :
                        persistenceSetupClass.getAnnotation(Projections.class);

        if(projections == null) {
            return "";
        }

        return Stream.of(projections.value()).map(this::resolveCauseTypes)
                .collect(Collectors.joining(";"));
    }

    private String resolveCauseTypes(final Projection projection) {
        return typeRetriever.typesFrom(projection, annotation -> projection.becauseOf())
                .stream().map(typeElement -> typeElement.getSimpleName().toString())
                .map(name -> "\"" + name + "\"").collect(Collectors.joining(", "));
    }

}
