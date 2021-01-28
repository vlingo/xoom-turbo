// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation;

import static javax.tools.Diagnostic.Kind.ERROR;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

public abstract class AnnotationProcessor extends AbstractProcessor {

    protected ProcessingEnvironment environment;

    @Override
    public synchronized void init(final ProcessingEnvironment environment) {
        super.init(environment);
        this.environment = environment;
    }

    @Override
    public boolean process(final Set<? extends TypeElement> set,
                           final RoundEnvironment roundEnvironment) {
        final AnnotatedElements annotatedElements =
                AnnotatedElements.from(roundEnvironment, supportedAnnotationClasses());

        if(annotatedElements.exists()) {
            try {
                generate(annotatedElements);
            } catch (final ProcessingAnnotationException exception) {
                printError(environment.getMessager(), exception);
            }
        }

        return true;
    }

//    @SuppressWarnings({ "rawtypes", "unchecked" })
//    private Map<Class, Set<Element>> filterAnnotatedElements(final RoundEnvironment environment) {
//        final Function<Class, SimpleEntry<Class, Set<Element>>> mapper =
//                annotationClass ->
//                        new SimpleEntry<Class, Set<Element>>(
//                                annotationClass, environment.getElementsAnnotatedWith(annotationClass));
//
//        return supportedAnnotationClasses().map(mapper)
//                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
//    }

    protected abstract void generate(final AnnotatedElements annotatedElements);

    @SuppressWarnings("rawtypes")
    public abstract Stream<Class> supportedAnnotationClasses();

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
        return supportedAnnotationClasses().map(Class::getCanonicalName).collect(Collectors.toSet());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

}
