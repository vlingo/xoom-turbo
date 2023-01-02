// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.autodispatch;

import io.vlingo.xoom.http.Method;
import io.vlingo.xoom.turbo.annotation.ProcessingAnnotationException;
import io.vlingo.xoom.turbo.annotation.TypeRetriever;
import io.vlingo.xoom.turbo.annotation.Validation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import java.util.regex.PatternSyntaxException;

public interface AutoDispatchValidations extends Validation {

  static Validation isProtocolModelAnInterface() {
    return (processingEnvironment, annotation, annotatedElements) -> {
      annotatedElements.elementsWith(annotation).forEach(element -> {
        final Model model = element.getAnnotation(Model.class);
        final TypeRetriever retriever = TypeRetriever.with(processingEnvironment);
        if (!retriever.isAnInterface(model, Void -> model.protocol())) {
          throw new ProcessingAnnotationException(
                  String.format("Class [%s]. Protocol value to Model annotation must be an interface",
                          retriever.getClassName(model, Void -> model.protocol())));
        }
      });
    };
  }

  static Validation bodyForRouteValidator() {
    return (processingEnvironment, annotation, annotatedElements) -> {
      annotatedElements.elementsWith(annotation).forEach(rootElement -> {
        rootElement.getEnclosedElements().forEach(enclosed -> {
          final Route routeAnnotation = enclosed.getAnnotation(Route.class);
          if (ElementKind.METHOD.equals(enclosed.getKind()) && routeAnnotation != null && routeAnnotation.method() == Method.GET) {
            final ExecutableElement executableElement = (ExecutableElement) enclosed;
            executableElement.getParameters().forEach(methodParameters -> {
              if (ElementKind.PARAMETER.equals(methodParameters.getKind()) && methodParameters.getAnnotation(Body.class) != null) {
                throw new ProcessingAnnotationException(
                        String.format("Class [%s]. Body annotation is not allowed with %s as method parameter for Route annotation.",
                                getQualifiedClassName(processingEnvironment, rootElement),
                                routeAnnotation.method().name));
              }
            });
          }
        });
      });
    };
  }

  static Validation modelWithoutQueryValidator() {
    return (processingEnvironment, annotation, annotatedElements) -> {
      annotatedElements.elementsWith(annotation).forEach(rootElement -> {
        if (rootElement.getAnnotation(Queries.class) == null) {
          rootElement.getEnclosedElements().forEach(enclosed -> {
            final Route routeAnnotation = enclosed.getAnnotation(Route.class);
            if (ElementKind.METHOD.equals(enclosed.getKind()) && routeAnnotation != null && routeAnnotation.method() == Method.GET) {
              throw new ProcessingAnnotationException(
                      String.format("Class [%s]. Class with %s method for Route need to have Queries annotation.",
                              getQualifiedClassName(processingEnvironment, rootElement),
                              routeAnnotation.method().name));
            }
          });
        }
      });
    };
  }

  static Validation isQueriesProtocolAnInterface() {
    return (processingEnvironment, annotation, annotatedElements) -> {
      annotatedElements.elementsWith(annotation).forEach(element -> {
        final Queries queries = element.getAnnotation(Queries.class);
        final TypeRetriever retriever = TypeRetriever.with(processingEnvironment);
        if (!retriever.isAnInterface(queries, Void -> queries.protocol())) {
          throw new ProcessingAnnotationException(
                  String.format("Class [%s]. Protocol value to Queries annotation must be an interface.",
                          getQualifiedClassName(processingEnvironment, element),
                          retriever.getClassName(queries,
                                  Void -> queries.protocol())));
        }
      });
    };
  }


  static Validation queryWithoutModelValidator() {
    return (processingEnvironment, annotation, annotatedElements) -> {
      annotatedElements.elementsWith(annotation).forEach(rootElement -> {
        if (rootElement.getAnnotation(Model.class) == null) {
          rootElement.getEnclosedElements().forEach(enclosed -> {
            final Route routeAnnotation = enclosed.getAnnotation(Route.class);
            if (ElementKind.METHOD.equals(enclosed.getKind()) && routeAnnotation != null && (routeAnnotation.method() == Method.POST || routeAnnotation.method() == Method.PUT
                    || routeAnnotation.method() == Method.PATCH || routeAnnotation.method() == Method.DELETE)) {
              throw new ProcessingAnnotationException(
                      String.format("Class [%s]. Class with %s method for Route need to have Model annotation.",
                              getQualifiedClassName(processingEnvironment, rootElement),
                              routeAnnotation.method().name));
            }
          });
        }
      });
    };
  }

  static Validation routeHasQueryOrModel() {
    return (processingEnvironment, annotation, annotatedElements) -> {
      annotatedElements.elementsWith(annotation).forEach(methodElement -> {
        final ExecutableElement executableElement = (ExecutableElement) methodElement;
        final Queries queriesAnnotation = executableElement.getEnclosingElement().getAnnotation(Queries.class);
        final Model modelAnnotation = executableElement.getEnclosingElement().getAnnotation(Model.class);
        if (queriesAnnotation == null && modelAnnotation == null) {
          throw new ProcessingAnnotationException(
                  String.format("Class [%s]. To use Route annotation you need to use Queries or Model annotation on the Class level.",
                          getQualifiedClassName(processingEnvironment, methodElement.getEnclosingElement())));
        }
      });
    };
  }

  static Validation routeWithoutResponseValidator() {
    return (processingEnvironment, annotation, annotatedElements) -> {
      annotatedElements.elementsWith(annotation).forEach(rootElement -> {
        if (rootElement.getAnnotation(Model.class) != null) {
          rootElement.getEnclosedElements().forEach(enclosed -> {
            final Route routeAnnotation = enclosed.getAnnotation(Route.class);
            boolean hasMethods = ElementKind.METHOD.equals(enclosed.getKind()) && routeAnnotation != null && (routeAnnotation.method() == Method.POST || routeAnnotation.method() == Method.PUT
                    || routeAnnotation.method() == Method.PATCH || routeAnnotation.method() == Method.DELETE);
            if (hasMethods && enclosed.getAnnotation(ResponseAdapter.class) == null) {
              throw new ProcessingAnnotationException(
                      String.format("Class [%s]. Class with %s method for Route need to have Response annotation.",
                              getQualifiedClassName(processingEnvironment, rootElement),
                              routeAnnotation.method().name));
            }
          });
        }
      });
    };
  }

  static Validation handlerWithoutValidMethodValidator() {
    return (processingEnvironment, annotation, annotatedElements) -> {
      annotatedElements.elementsWith(annotation).forEach(rootElement -> {
//                final Model model = rootElement.getAnnotation(Model.class);
//                if (model != null) {
//                    rootElement.getEnclosedElements().forEach(enclosed -> {
//                        final Route routeAnnotation = enclosed.getAnnotation(Route.class);
//                        if(ElementKind.METHOD.equals(enclosed.getKind()) && routeAnnotation != null){
//                            final String handler = routeAnnotation.handler();
//                            final String methodName = getMethodName(processingEnvironment, rootElement, handler);
//                            final String[] params = getParams(processingEnvironment, rootElement, handler);
//                            final List<ExecutableElement> methods =
//                                    TypeRetriever.with(processingEnvironment)
//                                            .getMethods(model, Void -> model.protocol());
//                            methods.stream()
//                                    .filter(m -> methodName.equals(m.getSimpleName().toString()))
//                                    .forEach(m -> {
//                                if(params.length < m.getParameters().size()){
//                                    throw new ProcessingAnnotationException(
//                                            String.format("Class [%s], with Model annotation, have Route annotation with an invalid protocol handler: %s",
//                                                    getQualifiedClassName(processingEnvironment, rootElement),
//                                                    m.toString())
//                                    );
//                                }
//                            });
//                        }
//                    });
//                }
      });
    };
  }

  static Validation hasAutoDispatchAnnotation() {
    return (processingEnvironment, annotation, annotatedElements) -> {
      annotatedElements.elementsWith(annotation).forEach(rootElement -> {
        if (rootElement.getAnnotation(AutoDispatch.class) == null) {
          throw new ProcessingAnnotationException(
                  String.format("Class [%s]. Class annotated with %s needs AutoDispatch annotation.",
                          getQualifiedClassName(processingEnvironment, rootElement),
                          annotation.getClass().getName()));
        }
      });
    };
  }


//    Use $ prefix which differs ResourceHandler members to endpoint params

  static Validation handlerTypeValidation() {
    return (processingEnvironment, annotation, annotatedElements) -> {
      annotatedElements.elementsWith(annotation).forEach(rootElement -> {
        final AutoDispatch autoDispatch = rootElement.getAnnotation(AutoDispatch.class);
        final TypeRetriever retriever = TypeRetriever.with(processingEnvironment);
        final TypeElement typeElement = retriever.getTypeElement(autoDispatch, Void -> autoDispatch.handlers());
        if (!typeElement.getModifiers().contains(Modifier.PUBLIC)) {
          throw new ProcessingAnnotationException(
                  String.format("Class [%s]. Handler Class %s needs to be public.",
                          getQualifiedClassName(processingEnvironment, rootElement),
                          typeElement.getSimpleName()));
        }
        typeElement.getEnclosedElements().forEach(element -> {
          if (element.getKind().equals(ElementKind.METHOD)) {
            throw new ProcessingAnnotationException(
                    String.format("Class [%s]. Methods are not allowed on handler %s",
                            getQualifiedClassName(processingEnvironment, rootElement),
                            typeElement.getSimpleName()));
          }
          if (element.getKind().equals(ElementKind.FIELD)) {
            final VariableElement variable = (VariableElement) element;
            if (variable.getModifiers().size() != 3 || (!variable.getModifiers().contains(Modifier.PUBLIC) ||
                    !variable.getModifiers().contains(Modifier.STATIC) && !variable.getModifiers().contains(Modifier.FINAL))) {
              throw new ProcessingAnnotationException(
                      String.format("Class [%s]. Fields of handler %s must have public final static modifiers.",
                              getQualifiedClassName(processingEnvironment, rootElement),
                              typeElement.getSimpleName()));

            }
            if (variable.asType().getKind().equals(TypeKind.DECLARED)) {
              final DeclaredType type = (DeclaredType) variable.asType();
              if (!HandlerEntry.class.getName().equals(type.asElement().toString())) {
                throw new ProcessingAnnotationException(
                        String.format("Class [%s]. Fields of handler %s must have type int, Integer or HandlerEntry.",
                                getQualifiedClassName(processingEnvironment, rootElement),
                                typeElement.getSimpleName()));

              }
              return;
            }
            if (variable.asType().getKind().equals(TypeKind.INT)) {
              return;
            }
            throw new ProcessingAnnotationException(
                    String.format("Class [%s]. Fields of handler %s must have type int, Integer or HandlerEntry.",
                            getQualifiedClassName(processingEnvironment, rootElement),
                            typeElement.getSimpleName()));
          }
        });
      });
    };
  }

  static String[] getParams(final ProcessingEnvironment processingEnvironment, final Element rootElement, final String handler) {
    try {
      return handler.substring(handler.indexOf("(") + 1, handler.indexOf(")")).split(",");
    } catch (StringIndexOutOfBoundsException | ArrayIndexOutOfBoundsException | PatternSyntaxException ex) {
      throw new ProcessingAnnotationException(
              String.format("Class [%s], with Model annotation, have Route annotation with an invalid protocol handler: %s",
                      getQualifiedClassName(processingEnvironment, rootElement),
                      handler)
      );
    }
  }

  static String getMethodName(final ProcessingEnvironment processingEnvironment, final Element rootElement, final String handler) {
    try {
      return handler.substring(0, handler.indexOf("("));
    } catch (StringIndexOutOfBoundsException | ArrayIndexOutOfBoundsException | PatternSyntaxException ex) {
      throw new ProcessingAnnotationException(
              String.format("Class [%s], with Model annotation, have Route annotation with an invalid protocol handler: %s",
                      getQualifiedClassName(processingEnvironment, rootElement),
                      handler)
      );
    }
  }


  static String getQualifiedClassName(final ProcessingEnvironment processingEnvironment, final Element rootElement) {
    return String.format("%s.%s.java", processingEnvironment.getElementUtils().getPackageOf(rootElement).getQualifiedName().toString(), rootElement.getSimpleName().toString());
  }

  static Validation entityActorValidation() {
    return (processingEnvironment, annotation, annotatedElements) -> {
      annotatedElements.elementsWith(annotation).forEach(rootElement -> {
        final Model model = rootElement.getAnnotation(Model.class);
        final TypeElement genericType =
                TypeRetriever.with(processingEnvironment)
                        .getGenericType(model, Void -> model.actor());

        if (genericType != null) {
          final boolean hasId = genericType.getEnclosedElements().stream().anyMatch(e ->
                  e.getKind().equals(ElementKind.FIELD) && e.getSimpleName().toString().equals("id") && e.getModifiers().contains(Modifier.PUBLIC)
          );
          if (!hasId) {
            throw new ProcessingAnnotationException(
                    String.format("Class [%s], with Model annotation, has an actor state object without an public id: %s",
                            getQualifiedClassName(processingEnvironment, rootElement), genericType.getSimpleName())
            );
          }
        }
      });
    };
  }
}
