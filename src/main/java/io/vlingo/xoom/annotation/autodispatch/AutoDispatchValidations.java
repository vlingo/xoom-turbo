package io.vlingo.xoom.annotation.autodispatch;

import io.vlingo.http.Method;
import io.vlingo.xoom.annotation.AnnotatedElements;
import io.vlingo.xoom.annotation.ProcessingAnnotationException;
import io.vlingo.xoom.annotation.TypeRetriever;
import io.vlingo.xoom.annotation.Validation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;

public interface AutoDispatchValidations extends Validation {

    static Validation isModelInterface() {
        return (processingEnvironment, annotation, annotatedElements) -> {
            annotatedElements.elementsWith(Model.class).forEach(element -> {
                final Model queries = element.getAnnotation(Model.class);
                final TypeRetriever retriever = TypeRetriever.with(processingEnvironment);
                if (!retriever.isAnInterface(queries, Void -> queries.protocol())) {
                    throw new ProcessingAnnotationException(
                            String.format("Protocol value to Model annotation must be an interface, class informed: %s",
                                    retriever.getClassName(queries,
                                            Void -> queries.protocol())));
                }
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
                            throw new ProcessingAnnotationException("Class with " +  routeAnnotation.method().name + " method for Route need to have Queries annotation.");
                        }
                    });
                }
            });
        };
    }

    static Validation isQueryInterface() {
        return (processingEnvironment, annotation, annotatedElements) -> {
            annotatedElements.elementsWith(Queries.class).forEach(element -> {
                final Queries queries = element.getAnnotation(Queries.class);
                final TypeRetriever retriever = TypeRetriever.with(processingEnvironment);
                if (!retriever.isAnInterface(queries, Void -> queries.protocol())) {
                    throw new ProcessingAnnotationException(
                            String.format("Protocol value to Queries annotation must be an interface, class informed: %s",
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
                            throw new ProcessingAnnotationException("Class with " +  routeAnnotation.method().name + " method for Route need to have Model annotation.");
                        }
                    });
                }
            });
        };
    }
}
