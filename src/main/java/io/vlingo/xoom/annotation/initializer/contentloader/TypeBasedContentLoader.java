// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer.contentloader;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class TypeBasedContentLoader {

    protected final Element annotatedClass;
    protected final ProcessingEnvironment environment;

    protected TypeBasedContentLoader(final Element annotatedClass,
                                     final ProcessingEnvironment environment) {
        this.environment = environment;
        this.annotatedClass = annotatedClass;
    }

    public void load(final CodeGenerationContext context) {
        this.retrieveTypes()
                .forEach(typeElement -> context.addContent(standard(), typeElement));
    }

    protected TypeElement toType(final TypeMirror typeMirror) {
        return (TypeElement) environment.getTypeUtils().asElement(typeMirror);
    }

    protected TypeElement retrieveType(final Object annotation, final Function<Object, Class<?>> retriever) {
        try {
            final Class<?> clazz =
                    retriever.apply(annotation);

            return environment.getElementUtils()
                    .getTypeElement(clazz.getCanonicalName());
        } catch (final MirroredTypeException exception) {
            return (TypeElement) environment.getTypeUtils()
                    .asElement(exception.getTypeMirror());
        }
    }

    protected List<TypeElement> retrieveTypes(final Object annotation, final Function<Object, Class<?>[]> retriever) {
        try {
            final Class<?>[] classes =
                    retriever.apply(annotation);

            return Stream.of(classes).map(clazz -> environment.getElementUtils()
                    .getTypeElement(clazz.getCanonicalName())).collect(Collectors.toList());
        } catch (final MirroredTypesException exception) {
            return exception.getTypeMirrors().stream()
                    .map(typeMirror -> (TypeElement) environment.getTypeUtils()
                            .asElement(typeMirror)).collect(Collectors.toList());
        }
    }

    protected abstract List<TypeElement> retrieveTypes();

    protected abstract TemplateStandard standard();

}
