// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation;

import io.vlingo.xoom.annotation.initializer.Xoom;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import java.util.Map;
import java.util.Set;

public interface Validation {

    void validate(final Class annotation, final Map<Class, Set<Element>> annotatedElements);

    static Validation singularityValidation() {
        return (annotation, annotatedElements) -> {
            if(annotatedElements.get(annotation).size() > 1) {
                throw new ProcessingAnnotationException("Only one class should be annotated with" + annotation.getName());
            }
        };
    }

    static Validation targetValidation() {
        return (annotation, annotatedElements) -> {
            annotatedElements.get(annotation).forEach(rootElement -> {
                if (rootElement.getKind() != ElementKind.CLASS) {
                    throw new ProcessingAnnotationException("The " + annotation.getName() + " annotation is only allowed at class level");
                }
            });
        };
    }

    static Validation classVisibilityValidation() {
        return (annotation, annotatedElements) -> {
            annotatedElements.get(annotation).forEach(element -> {
                if (!element.getModifiers().contains(Modifier.PUBLIC)) {
                    throw new ProcessingAnnotationException("The class " + element.getSimpleName() + " is not public.");
                }
            });
        };
    }

 }
