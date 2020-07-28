// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
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
    public boolean process(final Set<? extends TypeElement> set,
                           final RoundEnvironment roundEnvironment) {
        final Map<Class, Set<Element>> annotatedElements =
                filterAnnotatedElements(roundEnvironment);

        if(!annotatedElements.isEmpty()) {
            try {
                generate(annotatedElements);
            } catch (final ProcessingAnnotationException exception) {
                printError(environment.getMessager(), exception);
            }
        }

        return true;
    }

    private Map<Class, Set<Element>> filterAnnotatedElements(final RoundEnvironment environment) {
        final Function<Class, SimpleEntry<Class, Set<Element>>> mapper =
                annotationClass ->
                        new SimpleEntry<Class, Set<Element>>(
                                annotationClass, environment.getElementsAnnotatedWith(annotationClass));

        return supportedAnnotationClasses().map(mapper)
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    protected abstract void generate(final Map<Class, Set<Element>> annotatedElements);

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
