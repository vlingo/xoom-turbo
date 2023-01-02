// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TypeRetriever {

  @SuppressWarnings("unused")
  private static TypeRetriever instance;
  private final Elements elements;
  private final ProcessingEnvironment environment;

  private TypeRetriever(final ProcessingEnvironment environment) {
    this.environment = environment;
    this.elements = environment.getElementUtils();
  }

  @SuppressWarnings("rawtypes")
  public Stream<TypeMirror> subclassesOf(final Class superclass,
                                         final String[] packages) {
    return Stream.of(packages).filter(this::isValidPackage)
            .map(packageName -> elements.getPackageElement(packageName))
            .flatMap(packageElement -> packageElement.getEnclosedElements().stream())
            .filter(element -> isSubclass(element, superclass))
            .map(element -> element.asType());
  }

  @SuppressWarnings("rawtypes")
  private boolean isSubclass(final Element typeElement, final Class superclass) {
    if (superclass.isInterface()) {
      return ((TypeElement) typeElement).getInterfaces().stream()
              .anyMatch(interfaceMirror -> {
                return interfaceMirror.toString().equals(superclass.getCanonicalName());
              });
    }

    final TypeMirror type =
            elements.getTypeElement(superclass.getCanonicalName())
                    .asType();

    return ((TypeElement) typeElement).getSuperclass()
            .equals(type);
  }

  public <T> TypeElement from(final T annotation, final Function<T, Class<?>> retriever) {
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

  public <T> List<TypeElement> typesFrom(final T annotation, final Function<T, Class<?>[]> retriever) {
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

  public static TypeRetriever with(final ProcessingEnvironment environment) {
    return new TypeRetriever(environment);
  }

  public boolean isAnInterface(final Annotation annotation, final Function<Object, Class<?>> retriever) {
    return getTypeElement(annotation, retriever).getKind().isInterface();
  }

  public String getClassName(final Annotation annotation, final Function<Object, Class<?>> retriever) {
    return getTypeElement(annotation, retriever).getQualifiedName().toString();
  }

  @SuppressWarnings("unchecked")
  public List<ExecutableElement> getMethods(final Annotation annotation, final Function<Object, Class<?>> retriever) {
    return (List<ExecutableElement>) getTypeElement(annotation, retriever).getEnclosedElements();
  }

  public TypeElement getGenericType(final Annotation annotation, final Function<Object, Class<?>> retriever) {
    final DeclaredType declaredType = (DeclaredType) getTypeElement(annotation, retriever).getSuperclass();
    if (declaredType.getTypeArguments().isEmpty()) {
      return null;
    }
    return (TypeElement) ((DeclaredType) declaredType.getTypeArguments().get(0)).asElement();
  }

  @SuppressWarnings("unchecked")
  public List<Element> getElements(final Annotation annotation, final Function<Object, Class<?>> retriever) {
    return (List<Element>) getTypeElement(annotation, retriever).getEnclosedElements();
  }

  public TypeElement getTypeElement(final Annotation annotation,
                                    final Function<Object, Class<?>> retriever) {
    return from(annotation, retriever);
  }

  public boolean isValidPackage(final String packageName) {
    return elements.getPackageElement(packageName) != null;
  }
}
