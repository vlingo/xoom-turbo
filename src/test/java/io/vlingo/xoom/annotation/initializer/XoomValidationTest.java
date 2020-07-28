// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer;

import io.vlingo.xoom.annotation.ProcessingAnnotationException;
import io.vlingo.xoom.annotation.Validation;
import org.junit.Test;
import org.mockito.Mockito;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static io.vlingo.xoom.annotation.initializer.AddressFactory.IdentityGenerator.DEFAULT;
import static io.vlingo.xoom.annotation.initializer.AddressFactory.IdentityGenerator.RANDOM;
import static io.vlingo.xoom.annotation.initializer.AddressFactory.Type.BASIC;
import static java.util.stream.Collectors.toSet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class XoomValidationTest {

    @Test
    public void testThatSingularityValidationPasses() {
        final Set<Element> annotatedElements = Mockito.mock(Set.class);
        when(annotatedElements.size()).thenReturn(1);
        Validation.singularityValidation().validate(Xoom.class, new HashMap<Class, Set<Element>>(){{put(Xoom.class, annotatedElements);}});
    }

    @Test(expected = ProcessingAnnotationException.class)
    public void testThatSingularityValidationFails() {
        final Set<Element> annotatedElements = Mockito.mock(Set.class);
        when(annotatedElements.size()).thenReturn(2);
        Validation.singularityValidation().validate(Xoom.class, new HashMap<Class, Set<Element>>(){{put(Xoom.class, annotatedElements);}});
    }

    @Test
    public void testThatTargetValidationPasses() {
        final Element element = Mockito.mock(Element.class);
        final Set<Element> annotatedElements = Stream.of(element).collect(toSet());
        when(element.getKind()).thenReturn(ElementKind.CLASS);
        Validation.targetValidation().validate(Xoom.class, new HashMap<Class, Set<Element>>(){{put(Xoom.class, annotatedElements);}});
    }

    @Test(expected = ProcessingAnnotationException.class)
    public void testThatTargetValidationFails() {
        final Element element = Mockito.mock(Element.class);
        final Set<Element> annotatedElements = Stream.of(element).collect(toSet());
        when(element.getKind()).thenReturn(ElementKind.METHOD);
        Validation.targetValidation().validate(Xoom.class, new HashMap<Class, Set<Element>>(){{put(Xoom.class, annotatedElements);}});
    }

    @Test
    public void testThatClassVisibilityValidationPasses() {
        final Element element = Mockito.mock(Element.class);
        final Set<Element> annotatedElements = Stream.of(element).collect(toSet());
        when(element.getModifiers()).thenReturn(Stream.of(Modifier.PUBLIC).collect(toSet()));
        Validation.classVisibilityValidation().validate(Xoom.class, new HashMap<Class, Set<Element>>(){{put(Xoom.class, annotatedElements);}});
    }

    @Test(expected = ProcessingAnnotationException.class)
    public void testThatClassVisibilityValidationFails() {
        final Element element = Mockito.mock(Element.class);
        final Set<Element> annotatedElements = Stream.of(element).collect(toSet());
        when(element.getModifiers()).thenReturn(Stream.of(Modifier.PRIVATE).collect(toSet()));
        Validation.classVisibilityValidation().validate(Xoom.class, new HashMap<Class, Set<Element>>(){{put(Xoom.class, annotatedElements);}});
    }

    @Test
    public void testThatAddressFactoryValidationPasses() {
        final Element element = Mockito.mock(Element.class);
        final Xoom xoom = createXoomAnnotation("annotation-test", true, BASIC, DEFAULT);
        when(element.getAnnotation(eq(Xoom.class))).thenReturn(xoom);
        final Set<Element> annotatedElements = Stream.of(element).collect(toSet());
        new AddressFactoryValidation().validate(Xoom.class, new HashMap<Class, Set<Element>>(){{put(Xoom.class, annotatedElements);}});
    }

    @Test(expected = ProcessingAnnotationException.class)
    public void testThatAddressFactoryValidationFails() {
        final Element element = Mockito.mock(Element.class);
        final Xoom xoom = createXoomAnnotation("annotation-test", false, BASIC, RANDOM);
        when(element.getAnnotation(eq(Xoom.class))).thenReturn(xoom);
        final Set<Element> annotatedElements = Stream.of(element).collect(toSet());
        new AddressFactoryValidation().validate(Xoom.class, new HashMap<Class, Set<Element>>(){{put(Xoom.class, annotatedElements);}});
    }

    private Xoom createXoomAnnotation(final String name,
                                      final boolean blocking,
                                      final AddressFactory.Type addressFactoryType,
                                      final AddressFactory.IdentityGenerator identityGenerator) {
      return new Xoom() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Xoom.class;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public boolean blocking() {
                return blocking;
            }

            @Override
            public AddressFactory addressFactory() {
                return new AddressFactory() {
                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return AddressFactory.class;
                    }

                    @Override
                    public Type type() {
                        return addressFactoryType;
                    }

                    @Override
                    public IdentityGenerator generator() {
                        return identityGenerator;
                    }
                };
            }
        };
    }

}
