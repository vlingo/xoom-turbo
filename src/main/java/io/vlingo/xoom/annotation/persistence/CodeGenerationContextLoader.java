// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.persistence;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.CodeGenerationParameter;
import io.vlingo.xoom.codegen.file.InternalTargetPathLocator;
import io.vlingo.xoom.codegen.template.projections.ProjectionType;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.CodeGenerationParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.*;
import static io.vlingo.xoom.codegen.template.storage.DatabaseType.IN_MEMORY;

public class CodeGenerationContextLoader {

    private final TypeElement annotatedClass;
    private final Persistence persistenceAnnotation;
    private final ProcessingEnvironment environment;

    public static CodeGenerationContext from(final ProcessingEnvironment environment,
                                             final Element annotatedClass) {
        return new CodeGenerationContextLoader((TypeElement) annotatedClass, environment).load();
    }

    private CodeGenerationContextLoader(final TypeElement annotatedClass,
                                        final ProcessingEnvironment environment) {
        this.annotatedClass = annotatedClass;
        this.persistenceAnnotation = annotatedClass.getAnnotation(Persistence.class);
        this.environment = environment;
    }

    private CodeGenerationContext load() {
        final String targetFolder =
                InternalTargetPathLocator.find(environment);

        final CodeGenerationContext context =
                CodeGenerationContext.generateInternallyTo(targetFolder)
                        .on(resolveParameters());

        loadStates(context);
        loadAggregates(context);
        loadDomainEventClasses(context);

        return context;
    }

    private void loadAggregates(final CodeGenerationContext context) {
        final Projections projections =
                annotatedClass.getAnnotation(Projections.class);

        if(projections != null) {
            retrieveAdaptableElements(projections, annotation -> projections.aggregateProtocols())
                    .forEach(element -> context.addContent(AGGREGATE_PROTOCOL, element));
        }
    }

    private void loadStates(final CodeGenerationContext context) {
        final StateAdapters stateAdapters =
                annotatedClass.getAnnotation(StateAdapters.class);

        if(stateAdapters != null) {
            Stream.of(stateAdapters.values())
                    .map(adapter -> retrieveAdaptableElement(adapter, anAdapter -> adapter.from()))
                    .forEach(element -> context.addContent(STATE, element));
        }
    }

    private void loadDomainEventClasses(final CodeGenerationContext context) {
        final EventAdapters eventAdapters =
                annotatedClass.getAnnotation(EventAdapters.class);

        if(eventAdapters != null) {
            Stream.of(eventAdapters.values())
                    .map(adapter -> retrieveAdaptableElement(adapter, anAdapter -> adapter.from()))
                    .forEach(element -> context.addContent(DOMAIN_EVENT, element));
        }
    }

    private String resolveProjection() {
        final Projections projections =
                annotatedClass.getAnnotation(Projections.class);

        if(projections == null) {
            return ProjectionType.NONE.name();
        }

        return projections.type().name();
    }

    private TypeElement retrieveAdaptableElement(final Object annotation, final Function<Object, Class<?>> retriever) {
        try {
            final Class<?> clazz =
                    retriever.apply(annotation);

            return environment.getElementUtils()
                    .getTypeElement(clazz.getCanonicalName());
        } catch (final MirroredTypeException exception) {
            return (TypeElement) environment.getTypeUtils()
                    .asElement(exception.getTypeMirror());
        }
    }

    private List<TypeElement> retrieveAdaptableElements(final Object annotation, final Function<Object, Class<?>[]> retriever) {
        try {
            final Class<?>[] classes =
                    retriever.apply(annotation);

            return Stream.of(classes).map(clazz -> environment.getElementUtils()
                    .getTypeElement(clazz.getCanonicalName())).collect(Collectors.toList());
        } catch (final MirroredTypesException exception) {
            return exception.getTypeMirrors().stream()
                    .map(typeMirror -> (TypeElement) environment.getTypeUtils()
                            .asElement(typeMirror)).collect(Collectors.toList());
        }
    }

    private Map<CodeGenerationParameter, String> resolveParameters() {
        return new HashMap<CodeGenerationParameter, String>() {{
            put(PROJECTIONS, resolveProjection());
            put(PACKAGE, persistenceAnnotation.basePackage());
            put(CQRS, String.valueOf(persistenceAnnotation.cqrs()));
            put(STORAGE_TYPE, persistenceAnnotation.storageType().name());
            put(DATABASE, IN_MEMORY.name());
            put(COMMAND_MODEL_DATABASE, IN_MEMORY.name());
            put(QUERY_MODEL_DATABASE, IN_MEMORY.name());
        }};
    }

}

