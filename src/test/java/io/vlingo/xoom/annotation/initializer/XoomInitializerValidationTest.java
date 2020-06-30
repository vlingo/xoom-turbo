// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.initializer;

import io.vlingo.xoom.annotation.ProcessingAnnotationException;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import java.util.Set;

public class XoomInitializerValidationTest {

    @Test
    public void testThatSingularityValidationPasses() {
        final Set<? extends Element> annotatedElements = Mockito.mock(Set.class);
        Mockito.when(annotatedElements.size()).thenReturn(1);
        XoomInitializerValidation.singularityValidation().validate(annotatedElements);
    }

    @Test(expected = ProcessingAnnotationException.class)
    public void testThatSingularityValidationFails() {
        final Set<? extends Element> annotatedElements = Mockito.mock(Set.class);
        Mockito.when(annotatedElements.size()).thenReturn(2);
        XoomInitializerValidation.singularityValidation().validate(annotatedElements);
    }

    @Test
    public void testThatTargetValidationPasses() {
        final Element element = Mockito.mock(Element.class);
        final Set<? extends Element> annotatedElements = Set.of(element);
        Mockito.when(element.getKind()).thenReturn(ElementKind.CLASS);
        XoomInitializerValidation.targetValidation().validate(annotatedElements);
    }

    @Test(expected = ProcessingAnnotationException.class)
    public void testThatTargetValidationFails() {
        final Element element = Mockito.mock(Element.class);
        final Set<? extends Element> annotatedElements = Set.of(element);
        Mockito.when(element.getKind()).thenReturn(ElementKind.METHOD);
        XoomInitializerValidation.targetValidation().validate(annotatedElements);
    }

    @Test
    public void testThatClassVisibilityValidationPasses() {
        final Element element = Mockito.mock(Element.class);
        final Set<? extends Element> annotatedElements = Set.of(element);
        Mockito.when(element.getModifiers()).thenReturn(Set.of(Modifier.PUBLIC));
        XoomInitializerValidation.classVisibilityValidation().validate(annotatedElements);
    }

    @Test(expected = ProcessingAnnotationException.class)
    public void testThatClassVisibilityValidationFails() {
        final Element element = Mockito.mock(Element.class);
        final Set<? extends Element> annotatedElements = Set.of(element);
        Mockito.when(element.getModifiers()).thenReturn(Set.of(Modifier.PRIVATE));
        XoomInitializerValidation.classVisibilityValidation().validate(annotatedElements);
    }

}
