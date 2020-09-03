// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer;

import io.vlingo.xoom.XoomInitializationAware;
import io.vlingo.xoom.annotation.TypeRetriever;
import io.vlingo.xoom.annotation.persistence.Persistence;
import io.vlingo.xoom.annotation.persistence.Projection;
import io.vlingo.xoom.annotation.persistence.Projections;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameters;
import io.vlingo.xoom.codegen.template.projections.ProjectionType;
import io.vlingo.xoom.codegen.template.storage.StorageType;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.XOOM_INITIALIZER;

public class CodeGenerationParameterResolver {

    private final String basePackage;
    private final ProcessingEnvironment environment;
    private final TypeElement bootstrapClass;
    private final TypeElement persistenceSetupClass;
    private final Set<TypeElement> autoDispatchResourceClasses = new HashSet<>();

    public static CodeGenerationParameterResolver from(final String basePackage,
                                                       final TypeElement bootstrapClass,
                                                       final TypeElement persistenceSetupClass,
                                                       final Set<TypeElement> autoDispatchResourceClasses,
                                                       final ProcessingEnvironment environment) {
        return new CodeGenerationParameterResolver(basePackage, bootstrapClass,
                persistenceSetupClass, autoDispatchResourceClasses, environment);
    }

    private CodeGenerationParameterResolver(final String basePackage,
                                            final TypeElement bootstrapClass,
                                            final TypeElement persistenceSetupClass,
                                            final Set<TypeElement> autoDispatchResourceClasses,
                                            final ProcessingEnvironment environment) {
        this.basePackage = basePackage;
        this.bootstrapClass = bootstrapClass;
        this.persistenceSetupClass = persistenceSetupClass;
        this.autoDispatchResourceClasses.addAll(autoDispatchResourceClasses);
        this.environment = environment;
    }

    public CodeGenerationParameters resolve() {
        return CodeGenerationParameters.from(PACKAGE, resolveBasePackage())
                .add(STORAGE_TYPE, resolveStorageType())
                .add(APPLICATION_NAME, resolveApplicationName())
                .add(ADDRESS_FACTORY, resolveAddressFactoryType())
                .add(IDENTITY_GENERATOR, resolveIdentityGeneratorType())
                .add(ANNOTATIONS, Boolean.FALSE.toString())
                .add(BLOCKING_MESSAGING, resolveBlockingMessaging())
                .add(XOOM_INITIALIZER_NAME, resolveInitializerClass())
                .add(PROJECTION_TYPE, resolveProjections())
                .add(PROJECTABLES, resolveProjectables())
                .add(REST_RESOURCES, resolveRestResources())
                .add(CQRS, resolveCQRS());
    }

    private String resolveApplicationName() {
        return bootstrapClass.getAnnotation(Xoom.class).name();
    }

    private String resolveStorageType() {
        if(persistenceSetupClass == null) {
            return StorageType.NONE.name();
        }
        return persistenceSetupClass.getAnnotation(Persistence.class)
                .storageType().name();
    }

    private String resolveBasePackage() {
        return basePackage;
    }

    private String resolveCQRS() {
        if(persistenceSetupClass == null) {
            return String.valueOf(false);
        }
        return String.valueOf(persistenceSetupClass
                .getAnnotation(Persistence.class).cqrs());
    }

    private String resolveInitializerClass() {
        if(hasInitializerSubClass()) {
            return bootstrapClass.getSimpleName().toString();
        }
        return XOOM_INITIALIZER.resolveClassname();
    }

    private boolean hasInitializerSubClass() {
        final String initializerInterfaceName =
                XoomInitializationAware.class.getCanonicalName();

        final TypeMirror xoomInitializerAwareInterface =
                environment.getElementUtils()
                        .getTypeElement(initializerInterfaceName).asType();

        return bootstrapClass.getInterfaces().contains(xoomInitializerAwareInterface);
    }

    private String resolveBlockingMessaging() {
        return String.valueOf(bootstrapClass.getAnnotation(Xoom.class).blocking());
    }

    private String resolveAddressFactoryType() {
        return bootstrapClass.getAnnotation(Xoom.class).addressFactory().type().name();
    }

    private String resolveIdentityGeneratorType() {
        final Xoom xoomAnnotation =
                bootstrapClass.getAnnotation(Xoom.class);

        final AddressFactory.Type addressFactoryType =
                xoomAnnotation.addressFactory().type();

        return xoomAnnotation.addressFactory()
                .generator().resolveWith(addressFactoryType).name();
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

    private String resolveRestResources() {
        return autoDispatchResourceClasses.stream().map(type -> type.getSimpleName().toString())
                .map(resourceName -> resourceName + "Handler").collect(Collectors.joining(";"));
    }

    private String resolveCauseTypes(final Projection projection) {
        return TypeRetriever.with(environment).typesFrom(projection, annotation -> projection.becauseOf())
                .stream().map(typeElement -> typeElement.getSimpleName().toString())
                .map(name -> "\"" + name + "\"").collect(Collectors.joining(", "));

    }

}