// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.stream.Stream;

public class ClassRetriever {

    private static ClassRetriever instance;
    private final Elements elements;

    private ClassRetriever(final Elements elements){
        this.elements = elements;
    }

    public Stream<TypeMirror> subclassesOf(final Class superclass,
                                         final String[] packages) {
        return Stream.of(packages).map(packageName -> elements.getPackageElement(packageName))
                .flatMap(packageElement -> packageElement.getEnclosedElements().stream())
                .filter(element -> isSubclass(element, superclass))
                .map(element -> element.asType());
    }

    private boolean isSubclass(final Element typeElement, final Class superclass) {
        final TypeMirror resourceHandler =
                elements.getTypeElement(superclass.getCanonicalName())
                        .asType();

        return ((TypeElement) typeElement).getSuperclass()
                .equals(resourceHandler);
    }

    public static ClassRetriever with(final Elements elements) {
        return new ClassRetriever(elements);
    }

}
