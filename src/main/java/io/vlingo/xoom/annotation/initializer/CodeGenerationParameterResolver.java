// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer;

import io.vlingo.xoom.XoomInitializationAware;
import io.vlingo.xoom.annotation.persistence.Persistence;
import io.vlingo.xoom.annotation.persistence.Projections;
import io.vlingo.xoom.codegen.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.projections.ProjectionType;
import io.vlingo.xoom.codegen.template.storage.StorageType;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.CodeGenerationParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.XOOM_INITIALIZER;
import static io.vlingo.xoom.codegen.template.storage.DatabaseType.IN_MEMORY;

public class CodeGenerationParameterResolver {

    private final String basePackage;
    private final TypeElement bootstrapClass;
    private final TypeElement persistenceSetupClass;
    private final ProcessingEnvironment environment;

    public static CodeGenerationParameterResolver from(final String basePackage,
                                                       final TypeElement bootstrapClass,
                                                       final TypeElement persistenceSetupClass,
                                                       final ProcessingEnvironment environment) {
        return new CodeGenerationParameterResolver(basePackage, bootstrapClass,
                persistenceSetupClass, environment);
    }

    private CodeGenerationParameterResolver(final String basePackage,
                                            final TypeElement bootstrapClass,
                                            final TypeElement persistenceSetupClass,
                                            final ProcessingEnvironment environment) {
        this.basePackage = basePackage;
        this.bootstrapClass = bootstrapClass;
        this.persistenceSetupClass = persistenceSetupClass;
        this.environment = environment;
    }

    public Map<CodeGenerationParameter, String> resolve() {
        return new HashMap<CodeGenerationParameter, String>() {{
            put(PACKAGE, resolveBasePackage());
            put(CQRS, resolveCQRS());
            put(STORAGE_TYPE, resolveStorageType());
            put(APPLICATION_NAME, resolveApplicationName());
            put(ADDRESS_FACTORY, resolveAddressFactoryType());
            put(IDENTITY_GENERATOR, resolveIdentityGeneratorType());
            put(ANNOTATIONS, Boolean.FALSE.toString());
            put(BLOCKING_MESSAGING, resolveBlockingMessaging());
            put(XOOM_INITIALIZER_NAME, resolveInitializerClass());
            put(PROJECTION_TYPE, resolveProjections());
            put(PROJECTABLES, resolveProjectables());
            put(DATABASE, IN_MEMORY.name());
            put(COMMAND_MODEL_DATABASE, IN_MEMORY.name());
            put(QUERY_MODEL_DATABASE, IN_MEMORY.name());
        }};
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
                persistenceSetupClass.getAnnotation(Projections.class);

        if(projections == null) {
            return ProjectionType.NONE.name();
        }

        return ProjectionType.CUSTOM.name();
    }

    private String resolveProjectables() {
        final Projections projections =
                persistenceSetupClass.getAnnotation(Projections.class);

        if(projections == null) {
            return "";
        }

        return "\"" + Stream.of(projections.value())
                .map(projection -> String.join("\", \"", projection.becauseOf()))
                .collect(Collectors.joining("\";\"")) + "\"";
    }

}
