// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer;

import io.vlingo.xoom.annotation.AnnotatedElements;
import io.vlingo.xoom.annotation.ProcessingAnnotationException;
import io.vlingo.xoom.annotation.Validation;
import org.junit.Test;
import org.mockito.Mockito;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import java.lang.annotation.Annotation;
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
        final AnnotatedElements annotatedElements = Mockito.mock(AnnotatedElements.class);
        when(annotatedElements.count(Mockito.eq(Xoom.class))).thenReturn(1);
        Validation.singularityValidation().validate(Mockito.mock(ProcessingEnvironment.class), Xoom.class, annotatedElements);
    }

    @Test(expected = ProcessingAnnotationException.class)
    public void testThatSingularityValidationFails() {
        final AnnotatedElements annotatedElements = Mockito.mock(AnnotatedElements.class);
        when(annotatedElements.count(Mockito.eq(Xoom.class))).thenReturn(2);
        Validation.singularityValidation().validate(Mockito.mock(ProcessingEnvironment.class), Xoom.class, annotatedElements);
    }

    @Test
    public void testThatTargetValidationPasses() {
        final Element element = Mockito.mock(Element.class);
        final Set<Element> elements = Stream.of(element).collect(toSet());
        final AnnotatedElements annotatedElements = Mockito.mock(AnnotatedElements.class);
        when(annotatedElements.elementsWith(Mockito.eq(Xoom.class))).thenReturn(elements);
        when(element.getKind()).thenReturn(ElementKind.CLASS);
        Validation.targetValidation().validate(Mockito.mock(ProcessingEnvironment.class), Xoom.class, annotatedElements);
    }

    @Test(expected = ProcessingAnnotationException.class)
    public void testThatTargetValidationFails() {
        final Element element = Mockito.mock(Element.class);
        final Set<Element> elements = Stream.of(element).collect(toSet());
        final AnnotatedElements annotatedElements = Mockito.mock(AnnotatedElements.class);
        when(annotatedElements.elementsWith(Mockito.eq(Xoom.class))).thenReturn(elements);
        when(element.getKind()).thenReturn(ElementKind.METHOD);
        Validation.targetValidation().validate(Mockito.mock(ProcessingEnvironment.class), Xoom.class, annotatedElements);
    }

    @Test
    public void testThatClassVisibilityValidationPasses() {
        final Element element = Mockito.mock(Element.class);
        final Set<Element> elements = Stream.of(element).collect(toSet());
        final AnnotatedElements annotatedElements = Mockito.mock(AnnotatedElements.class);
        when(annotatedElements.elementsWith(Mockito.eq(Xoom.class))).thenReturn(elements);
        when(element.getModifiers()).thenReturn(Stream.of(Modifier.PUBLIC).collect(toSet()));
        Validation.classVisibilityValidation().validate(Mockito.mock(ProcessingEnvironment.class), Xoom.class, annotatedElements);
    }

    @Test(expected = ProcessingAnnotationException.class)
    public void testThatClassVisibilityValidationFails() {
        final Element element = Mockito.mock(Element.class);
        final Set<Element> elements = Stream.of(element).collect(toSet());
        final AnnotatedElements annotatedElements = Mockito.mock(AnnotatedElements.class);
        when(annotatedElements.elementsWith(Mockito.eq(Xoom.class))).thenReturn(elements);
        when(element.getModifiers()).thenReturn(Stream.of(Modifier.PRIVATE).collect(toSet()));
        Validation.classVisibilityValidation().validate(Mockito.mock(ProcessingEnvironment.class), Xoom.class, annotatedElements);
    }

    @Test
    public void testThatAddressFactoryValidationPasses() {
        final Element element = Mockito.mock(Element.class);
        final Set<Element> elements = Stream.of(element).collect(toSet());
        final Xoom xoom = createXoomAnnotation("annotation-test", true, BASIC, DEFAULT);
        final AnnotatedElements annotatedElements = Mockito.mock(AnnotatedElements.class);
        when(annotatedElements.elementsWith(Mockito.eq(Xoom.class))).thenReturn(elements);
        when(element.getAnnotation(eq(Xoom.class))).thenReturn(xoom);
        new AddressFactoryValidation().validate(Mockito.mock(ProcessingEnvironment.class), Xoom.class, annotatedElements);
    }

    @Test(expected = ProcessingAnnotationException.class)
    public void testThatAddressFactoryValidationFails() {
        final Element element = Mockito.mock(Element.class);
        final Set<Element> elements = Stream.of(element).collect(toSet());
        final Xoom xoom = createXoomAnnotation("annotation-test", false, BASIC, RANDOM);
        final AnnotatedElements annotatedElements = Mockito.mock(AnnotatedElements.class);
        when(annotatedElements.elementsWith(Mockito.eq(Xoom.class))).thenReturn(elements);
        when(element.getAnnotation(eq(Xoom.class))).thenReturn(xoom);
        new AddressFactoryValidation().validate(Mockito.mock(ProcessingEnvironment.class), Xoom.class, annotatedElements);
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
