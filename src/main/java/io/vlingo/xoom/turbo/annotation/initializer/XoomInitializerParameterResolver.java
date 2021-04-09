// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.initializer;

import io.vlingo.xoom.turbo.XoomInitializationAware;
import io.vlingo.xoom.turbo.codegen.parameter.CodeGenerationParameters;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import static io.vlingo.xoom.turbo.codegen.parameter.Label.*;
import static io.vlingo.xoom.turbo.codegen.template.TemplateStandard.XOOM_INITIALIZER;

public class XoomInitializerParameterResolver {

    private final String basePackage;
    private final ProcessingEnvironment environment;
    private final TypeElement bootstrapClass;

    public static XoomInitializerParameterResolver from(final String basePackage,
                                                        final TypeElement bootstrapClass,
                                                        final ProcessingEnvironment environment) {
        return new XoomInitializerParameterResolver(basePackage, bootstrapClass, environment);
    }

    private XoomInitializerParameterResolver(final String basePackage,
                                             final TypeElement bootstrapClass,
                                             final ProcessingEnvironment environment) {
        this.basePackage = basePackage;
        this.bootstrapClass = bootstrapClass;
        this.environment = environment;
    }

    public CodeGenerationParameters resolve() {
        return CodeGenerationParameters.from(PACKAGE, resolveBasePackage())
                .add(APPLICATION_NAME, resolveApplicationName())
                .add(USE_ANNOTATIONS, Boolean.FALSE.toString())
                .add(BLOCKING_MESSAGING, resolveBlockingMessaging())
                .add(XOOM_INITIALIZER_NAME, resolveInitializerClass());
    }

    private String resolveApplicationName() {
        return bootstrapClass.getAnnotation(Xoom.class).name();
    }

    private String resolveBasePackage() {
        return basePackage;
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

}