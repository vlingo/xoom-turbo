// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.autodispatch;

import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.http.Method;
import io.vlingo.xoom.lattice.model.EntityActor;
import io.vlingo.xoom.turbo.annotation.AnnotatedElements;
import io.vlingo.xoom.turbo.annotation.Validation;
import org.junit.Test;
import org.mockito.Mockito;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.vlingo.xoom.turbo.annotation.Validation.classVisibilityValidation;
import static io.vlingo.xoom.turbo.annotation.autodispatch.AutoDispatchValidations.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AutoDispatchValidatorTest {

    @Test
    public void testIsInterface() {
        final AnnotatedElements annotatedElements = mock(AnnotatedElements.class);
        final Element rootElement = mock(Element.class);
        Set<Element> elements = new HashSet<>();
        elements.add(rootElement);
        when(annotatedElements.elementsWith(Queries.class)).thenReturn(elements);
        when(rootElement.getKind()).thenReturn(ElementKind.INTERFACE);
        Validation.isInterface().validate(mock(ProcessingEnvironment.class), Queries.class, annotatedElements);
    }

    @Test
    public void testClassVisibilityValidation() {
        final AnnotatedElements annotatedElements = mock(AnnotatedElements.class);
        final Element rootElement = mock(Element.class);
        final Set<Element> elements = new HashSet<>();
        final Set<Modifier> modifiers = new HashSet<>();
        elements.add(rootElement);
        modifiers.add(Modifier.PUBLIC);
        when(rootElement.getModifiers()).thenReturn(modifiers);
        when(annotatedElements.elementsWith(Queries.class)).thenReturn(elements);
        classVisibilityValidation().validate(mock(ProcessingEnvironment.class), Queries.class, annotatedElements);
    }

    @Test
    public void testIsQueriesProtocolAnInterface() {
        final AnnotatedElements annotatedElements = mock(AnnotatedElements.class);
        final ProcessingEnvironment processingEnvironment = mock(ProcessingEnvironment.class);
        final TypeElement typeElement = mock(TypeElement.class);
        final Elements elementUtil = mock(Elements.class);
        final Element rootElement = mock(Element.class);
        final Set<Element> elements = new HashSet<>();
        elements.add(rootElement);
        when(rootElement.getAnnotation(Queries.class)).thenReturn(createQueriesAnnotation());
        when(annotatedElements.elementsWith(Queries.class)).thenReturn(elements);
        when(processingEnvironment.getElementUtils()).thenReturn(elementUtil);
        when(elementUtil.getTypeElement(Mockito.anyString())).thenReturn(typeElement);
        when(typeElement.getKind()).thenReturn(ElementKind.INTERFACE);
        isQueriesProtocolAnInterface().validate(processingEnvironment, Queries.class, annotatedElements);
    }

    @Test
    public void testQueryWithoutModelValidator() {
        final AnnotatedElements annotatedElements = mock(AnnotatedElements.class);
        final Element rootElement = mock(Element.class);
        final Element enclosedElement = mock(Element.class);
        final Set<Element> elements = new HashSet<>();
        final List enclosedElements = new ArrayList<>();
        elements.add(rootElement);
        enclosedElements.add(enclosedElement);
        when(annotatedElements.elementsWith(Queries.class)).thenReturn(elements);
        when(enclosedElement.getAnnotation(Route.class)).thenReturn(createRouteAnnotation(Method.GET));
        when(enclosedElement.getKind()).thenReturn(ElementKind.METHOD);
        when(rootElement.getEnclosedElements()).thenReturn(enclosedElements);
        queryWithoutModelValidator().validate(mock(ProcessingEnvironment.class), Queries.class, annotatedElements);
    }

    @Test
    public void testBodyForRouteValidator() {
        final AnnotatedElements annotatedElements = mock(AnnotatedElements.class);
        final ExecutableElement enclosedElement = mock(ExecutableElement.class);
        final Element rootElement = mock(Element.class);
        final VariableElement parameter = mock(VariableElement.class);
        final Set<Element> elements = new HashSet<>();
        final List enclosedElements = new ArrayList<>();
        final List variableElement = new ArrayList<>();
        elements.add(rootElement);
        enclosedElements.add(enclosedElement);
        variableElement.add(parameter);
        when(annotatedElements.elementsWith(Queries.class)).thenReturn(elements);
        when(rootElement.getEnclosedElements()).thenReturn(enclosedElements);
        when(enclosedElement.getParameters()).thenReturn(variableElement);
        when(enclosedElement.getAnnotation(Route.class)).thenReturn(createRouteAnnotation(Method.GET));
        when(enclosedElement.getKind()).thenReturn(ElementKind.METHOD);
        when(parameter.getKind()).thenReturn(ElementKind.PARAMETER);
        bodyForRouteValidator().validate(mock(ProcessingEnvironment.class), Queries.class, annotatedElements);
    }

    @Test
    public void testIsProtocolModelAnInterface() {
        final AnnotatedElements annotatedElements = mock(AnnotatedElements.class);
        final ProcessingEnvironment processingEnvironment = mock(ProcessingEnvironment.class);
        final TypeElement typeElement = mock(TypeElement.class);
        final Elements elementUtil = mock(Elements.class);
        final Element rootElement = mock(Element.class);
        final Set<Element> elements = new HashSet<>();
        elements.add(rootElement);
        when(rootElement.getAnnotation(Model.class)).thenReturn(createModelAnnotation());
        when(annotatedElements.elementsWith(Model.class)).thenReturn(elements);
        when(processingEnvironment.getElementUtils()).thenReturn(elementUtil);
        when(elementUtil.getTypeElement(Mockito.anyString())).thenReturn(typeElement);
        when(typeElement.getKind()).thenReturn(ElementKind.INTERFACE);
        isProtocolModelAnInterface().validate(processingEnvironment, Model.class, annotatedElements);
    }

    @Test
    public void testModelWithoutQueryValidator() {
        final AnnotatedElements annotatedElements = mock(AnnotatedElements.class);
        final Element rootElement = mock(Element.class);
        final Element enclosedElement = mock(Element.class);
        final Set<Element> elements = new HashSet<>();
        final List enclosedElements = new ArrayList<>();
        elements.add(rootElement);
        enclosedElements.add(enclosedElement);
        when(annotatedElements.elementsWith(Model.class)).thenReturn(elements);
        when(enclosedElement.getAnnotation(Route.class)).thenReturn(createRouteAnnotation(Method.PUT));
        when(enclosedElement.getKind()).thenReturn(ElementKind.METHOD);
        when(rootElement.getEnclosedElements()).thenReturn(enclosedElements);
        modelWithoutQueryValidator().validate(mock(ProcessingEnvironment.class), Model.class, annotatedElements);
    }

    @Test
    public void testRouteWithoutResponseValidator() {
        final AnnotatedElements annotatedElements = mock(AnnotatedElements.class);
        final Element rootElement = mock(Element.class);
        final Element enclosedElement = mock(Element.class);
        final Set<Element> elements = new HashSet<>();
        final List enclosedElements = new ArrayList<>();
        elements.add(rootElement);
        enclosedElements.add(enclosedElement);
        when(annotatedElements.elementsWith(Model.class)).thenReturn(elements);
        when(rootElement.getEnclosedElements()).thenReturn(enclosedElements);
        when(rootElement.getAnnotation(Model.class)).thenReturn(mock(Model.class));
        when(enclosedElement.getAnnotation(Route.class)).thenReturn(createRouteAnnotation(Method.PUT));
        when(enclosedElement.getAnnotation(ResponseAdapter.class)).thenReturn(mock(ResponseAdapter.class));
        when(enclosedElement.getKind()).thenReturn(ElementKind.METHOD);
        routeWithoutResponseValidator().validate(mock(ProcessingEnvironment.class), Model.class, annotatedElements);
    }

    @Test
    public void testHandlerWithoutValidMethodValidator() {
        final AnnotatedElements annotatedElements = mock(AnnotatedElements.class);
        final Element rootElement = mock(Element.class);
        final Element enclosedElement = mock(Element.class);
        final ExecutableElement method = mock(ExecutableElement.class);
        final Set<Element> elements = new HashSet<>();
        final List enclosedElements = new ArrayList<>();
        final List typeEnclosedElements = new ArrayList<>();
        final List variableElements = new ArrayList<>();
        final TypeElement typeElement = mock(TypeElement.class);
        final Elements elementUtil = mock(Elements.class);
        final ProcessingEnvironment processingEnvironment = mock(ProcessingEnvironment.class);
        final Name methodName = mock(Name.class);
        final VariableElement methodParameter = mock(VariableElement.class);
        typeEnclosedElements.add(method);
        variableElements.add(methodParameter);
        elements.add(rootElement);
        enclosedElements.add(enclosedElement);
        when(annotatedElements.elementsWith(Model.class)).thenReturn(elements);
        when(rootElement.getEnclosedElements()).thenReturn(enclosedElements);
        when(rootElement.getAnnotation(Model.class)).thenReturn(createModelAnnotation());
        when(enclosedElement.getAnnotation(Route.class)).thenReturn(createRouteAnnotation(Method.PUT));
        when(processingEnvironment.getElementUtils()).thenReturn(elementUtil);
        when(enclosedElement.getKind()).thenReturn(ElementKind.METHOD);
        when(elementUtil.getTypeElement(Mockito.anyString())).thenReturn(typeElement);
        when(typeElement.getEnclosedElements()).thenReturn(typeEnclosedElements);
        when(methodName.toString()).thenReturn("testMethod");
        when(method.getSimpleName()).thenReturn(methodName);
        when(method.getParameters()).thenReturn(variableElements);
        handlerWithoutValidMethodValidator().validate(processingEnvironment, Model.class, annotatedElements);
    }

    @Test
    public void testRouteHasQueryOrModel() {
        final AnnotatedElements annotatedElements = mock(AnnotatedElements.class);
        final ExecutableElement rootElement = mock(ExecutableElement.class);
        final ExecutableElement enclosingElement = mock(ExecutableElement.class);
        final Set<Element> elements = new HashSet<>();
        elements.add(rootElement);
        when(annotatedElements.elementsWith(Route.class)).thenReturn(elements);
        when(rootElement.getEnclosingElement()).thenReturn(enclosingElement);
        when(enclosingElement.getAnnotation(Queries.class)).thenReturn(mock(Queries.class));
        routeHasQueryOrModel().validate(mock(ProcessingEnvironment.class), Route.class, annotatedElements);
    }


    private Queries createQueriesAnnotation() {
        return new Queries() {
            @Override
            public Class<?> protocol() {
                return Protocol.class;
            }

            @Override
            public Class<? extends Actor> actor() {
                return null;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Queries.class;
            }
        };
    }

    private Route createRouteAnnotation(final Method method) {
        return new Route() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Route.class;
            }

            @Override
            public Method method() {
                return method;
            }

            @Override
            public String path() {
                return "";
            }

            @Override
            public int handler() {
                return 0;
            }
        };
    }

    private Model createModelAnnotation(){
        return new Model(){

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public Class<?> protocol() {
                return Protocol.class;
            }

            @Override
            public Class<? extends EntityActor> actor() {
                return null;
            }

            @Override
            public Class<?> data() {
                return null;
            }
        };
    }
    interface Protocol {
    }
}
