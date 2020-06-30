// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.initializer;

import com.google.auto.service.AutoService;
import io.vlingo.xoom.annotation.ProcessingAnnotationException;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.tools.Diagnostic.Kind.ERROR;

@AutoService(Processor.class)
public class XoomInitializerProcessor extends AbstractProcessor {

    private ProcessingEnvironment environment;

    @Override
    public synchronized void init(final ProcessingEnvironment environment) {
        super.init(environment);
        this.environment = environment;
    }

    @Override
    public boolean process(final Set<? extends TypeElement> set, final RoundEnvironment roundEnvironment) {
        final Set<? extends Element> annotatedElements = roundEnvironment.getElementsAnnotatedWith(Xoom.class);

        if(!annotatedElements.isEmpty()) {
            generate(annotatedElements);
        }

        return true;
    }

    private void generate(final Set<? extends Element> annotatedElements) {
        try {
            XoomInitializerValidator.instance().validate(annotatedElements);
            XoomInitializerGenerator.instance().generateFrom(environment, annotatedElements);
        } catch (final ProcessingAnnotationException exception) {
            printError(environment.getMessager(), exception);
        }
    }

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
        return Stream.of(Xoom.class.getCanonicalName()).collect(Collectors.toSet());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

}
