// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;

public interface Validation {

    void validate(final Class annotation, final AnnotatedElements annotatedElements);

    static Validation singularityValidation() {
        return (annotation, annotatedElements) -> {
            if(annotatedElements.count(annotation) > 1) {
                throw new ProcessingAnnotationException("Only one class should be annotated with" + annotation.getName());
            }
        };
    }

    static Validation targetValidation() {
        return (annotation, annotatedElements) -> {
            annotatedElements.elementsWith(annotation).forEach(rootElement -> {
                if (rootElement.getKind() != ElementKind.CLASS) {
                    throw new ProcessingAnnotationException("The " + annotation.getName() + " annotation is only allowed at class level");
                }
            });
        };
    }

    static Validation classVisibilityValidation() {
        return (annotation, annotatedElements) -> {
            annotatedElements.elementsWith(annotation).forEach(element -> {
                if (!element.getModifiers().contains(Modifier.PUBLIC)) {
                    throw new ProcessingAnnotationException("The class " + element.getSimpleName() + " is not public.");
                }
            });
        };
    }

 }
