// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnnotatedElements {

    private final Map<Class, Set<Element>> elements = new HashMap<>();

    public static AnnotatedElements from(final RoundEnvironment environment,
                                         final Stream<Class> supportedAnnotations) {
        final Function<Class, AbstractMap.SimpleEntry<Class, Set<Element>>> mapper =
                annotationClass ->
                        new AbstractMap.SimpleEntry<Class, Set<Element>>(
                                annotationClass, environment.getElementsAnnotatedWith(annotationClass));

        return new AnnotatedElements(supportedAnnotations.map(mapper)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    private AnnotatedElements(final Map<Class, Set<Element>> elements) {
        this.elements.putAll(elements);
    }

    public boolean exists() {
        return !elements.isEmpty();
    }

    public boolean hasElementsWith(final Class annotation) {
        return elements.containsKey(annotation) && !elementsWith(annotation).isEmpty();
    }

    public <T extends Element> Set<T> elementsWith(final Class ...annotations) {
        return Stream.of(annotations).map(annotation -> elements.get(annotation))
                .flatMap(set -> (Stream<T>) set.stream()).collect(Collectors.toSet());
    }

    public <T extends Element> T elementWith(final Class annotation) {
        return (T) elementsWith(annotation).stream().findFirst().orElse(null);
    }

    public int count(final Class annotation) {
        if(hasElementsWith(annotation)) {
            return 0;
        }
        return elementsWith(annotation).size();
    }
}
