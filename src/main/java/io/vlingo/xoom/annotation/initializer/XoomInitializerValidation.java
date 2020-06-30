// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.initializer;

import io.vlingo.xoom.annotation.ProcessingAnnotationException;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import java.util.Set;

public interface XoomInitializerValidation {

    void validate(final Set<? extends Element> annotatedElements);

    static XoomInitializerValidation singularityValidation() {
        return annotatedElements -> {
            if(annotatedElements.size() > 1) {
                throw new ProcessingAnnotationException("Only one class should be annotated with @Xoom");
            }
        };
    }

    static XoomInitializerValidation targetValidation() {
        return annotatedElements -> {
            annotatedElements.forEach(rootElement -> {
                if (rootElement.getKind() != ElementKind.CLASS) {
                    throw new ProcessingAnnotationException("Only classes can be annotated with @Xoom");
                }
            });
        };
    }

    static XoomInitializerValidation classVisibilityValidation() {
        return annotatedElements -> {
            annotatedElements.forEach(element -> {
                if (!element.getModifiers().contains(Modifier.PUBLIC)) {
                    throw new ProcessingAnnotationException("The class " + element.getSimpleName() + " is not public.");
                }
            });
        };
    }

     static XoomInitializerValidation addressFactoryValidation() {
         return annotatedElements -> {
             annotatedElements.forEach(element -> {
                 final Xoom xoom = element.getAnnotation(Xoom.class);
                 final AddressFactory addressFactory = xoom.addressFactory();
                 if(addressFactory.type().isBasic() && !addressFactory.generator().isDefault()) {
                     throw new ProcessingAnnotationException("The BasicAddressFactory only supports the Default generator.");
                 }
             });
         };
     }
 }
