// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.annotation;

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

  @SuppressWarnings("rawtypes")
  private final Map<Class, Set<Element>> elements = new HashMap<>();

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static AnnotatedElements from(final RoundEnvironment environment,
                                       final Stream<Class> supportedAnnotations) {
    final Function<Class, AbstractMap.SimpleEntry<Class, Set<Element>>> mapper =
            annotationClass ->
                    new AbstractMap.SimpleEntry<Class, Set<Element>>(
                            annotationClass, environment.getElementsAnnotatedWith(annotationClass));

    return new AnnotatedElements(supportedAnnotations.map(mapper)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
  }

  @SuppressWarnings("rawtypes")
  private AnnotatedElements(final Map<Class, Set<Element>> elements) {
    this.elements.putAll(elements);
  }

  public boolean exists() {
    return !elements.isEmpty();
  }

  @SuppressWarnings("rawtypes")
  public boolean hasElementsWith(final Class annotation) {
    return elements.containsKey(annotation) && !elementsWith(annotation).isEmpty();
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public <T extends Element> Set<T> elementsWith(final Class... annotations) {
    return Stream.of(annotations).filter(annotation -> elements.containsKey(annotation))
            .map(annotation -> elements.get(annotation)).flatMap(set -> (Stream<T>) set.stream())
            .collect(Collectors.toSet());
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public <T extends Element> T elementWith(final Class annotation) {
    return (T) elementsWith(annotation).stream().findFirst().orElse(null);
  }

  @SuppressWarnings("rawtypes")
  public int count(final Class annotation) {
    if (hasElementsWith(annotation)) {
      return 0;
    }
    return elementsWith(annotation).size();
  }
}
