// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation;

import io.vlingo.xoom.annotation.initializer.Xoom;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.tools.Diagnostic.Kind.ERROR;

public abstract class AnnotationProcessor extends AbstractProcessor {

    protected ProcessingEnvironment environment;

    @Override
    public synchronized void init(final ProcessingEnvironment environment) {
        super.init(environment);
        this.environment = environment;
    }

    @Override
    public boolean process(final Set<? extends TypeElement> set, final RoundEnvironment roundEnvironment) {
        final Set<? extends Element> annotatedElements =
                roundEnvironment.getElementsAnnotatedWith(annotationClass());

        if(!annotatedElements.isEmpty()) {
            try {
                generate(annotatedElements);
            } catch (final ProcessingAnnotationException exception) {
                printError(environment.getMessager(), exception);
            }
        }

        return true;
    }

    protected abstract void generate(final Set<? extends Element> annotatedElements);

    private void printError(final Messager messager,
                            final ProcessingAnnotationException exception) {
        if(exception.element == null) {
            messager.printMessage(ERROR, exception.getMessage());
        } else {
            messager.printMessage(ERROR, exception.getMessage(), exception.element);
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Stream.of(annotationClass().getCanonicalName()).collect(Collectors.toSet());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    public abstract Class annotationClass();

}
