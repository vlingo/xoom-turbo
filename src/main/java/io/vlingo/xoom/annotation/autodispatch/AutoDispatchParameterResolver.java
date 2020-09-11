// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.autodispatch;

import io.vlingo.xoom.annotation.TypeRetriever;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameters;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static io.vlingo.xoom.codegen.parameter.Label.*;
import static javax.lang.model.element.Modifier.DEFAULT;

public class AutoDispatchParameterResolver {

    private static final String ROUTE_SIGNATURE_PATTERN = "%s(%s)";

    private final TypeRetriever typeRetriever;
    private final ProcessingEnvironment environment;
    private final Set<TypeElement> autoDispatchResourceClasses = new HashSet<>();

    private AutoDispatchParameterResolver(final ProcessingEnvironment environment,
                                          final Set<TypeElement> autoDispatchResourceClasses) {
        this.environment = environment;
        this.typeRetriever = TypeRetriever.with(environment);
        this.autoDispatchResourceClasses.addAll(autoDispatchResourceClasses);
    }

    public static AutoDispatchParameterResolver from(final Set<TypeElement> autoDispatchResourceClasses,
                                                     final ProcessingEnvironment environment) {
        return new AutoDispatchParameterResolver(environment, autoDispatchResourceClasses);
    }

    public CodeGenerationParameters resolve() {
        final CodeGenerationParameters parameters =
                CodeGenerationParameters.from(USE_AUTO_DISPATCH,
                        !autoDispatchResourceClasses.isEmpty());

        final Consumer<CodeGenerationParameter> consumer =
                parameter -> parameters.add(parameter);

        this.autoDispatchResourceClasses.stream()
                .map(this::resolveAutoDispatchAnnotation).forEach(consumer);

        return parameters;
    }

    private CodeGenerationParameter resolveAutoDispatchAnnotation(final TypeElement autoDispatchClass) {
        final AutoDispatch autoDispatchAnnotation =
                autoDispatchClass.getAnnotation(AutoDispatch.class);

        final CodeGenerationParameter autoDispatchParameter =
                CodeGenerationParameter.of(AUTO_DISPATCH_NAME, autoDispatchClass.getQualifiedName())
                        .relate(URI_ROOT, autoDispatchAnnotation.path());

        resolveModelAnnotation(autoDispatchClass, autoDispatchParameter);
        resolveQueriesAnnotation(autoDispatchClass, autoDispatchParameter);
        resolveRoutesAnnotation(autoDispatchClass, autoDispatchParameter);
        return autoDispatchParameter;
    }

    private void resolveModelAnnotation(final TypeElement autoDispatchClass,
                                        final CodeGenerationParameter autoDispatchParameter) {
        final Model modelAnnotation =
                autoDispatchClass.getAnnotation(Model.class);

        if(modelAnnotation != null) {
            final TypeElement protocol =
                    typeRetriever.from(modelAnnotation, annotation -> modelAnnotation.protocol());

            final TypeElement actor =
                    typeRetriever.from(modelAnnotation, annotation -> modelAnnotation.actor());

            final TypeElement data =
                    typeRetriever.from(modelAnnotation, annotation -> modelAnnotation.data());

            autoDispatchParameter.relate(MODEL_PROTOCOL, protocol.getQualifiedName())
                    .relate(MODEL_ACTOR, actor.getQualifiedName())
                    .relate(MODEL_DATA, data.getQualifiedName());
        }
    }

    private void resolveQueriesAnnotation(final TypeElement autoDispatchClass,
                                          final CodeGenerationParameter autoDispatchParameter) {
        final Queries queriesAnnotation =
                autoDispatchClass.getAnnotation(Queries.class);

        if(queriesAnnotation != null) {
            final TypeElement protocol =
                    typeRetriever.from(queriesAnnotation, annotation -> queriesAnnotation.protocol());

            final TypeElement actor =
                    typeRetriever.from(queriesAnnotation, annotation -> queriesAnnotation.actor());

            autoDispatchParameter.relate(QUERIES_PROTOCOL, protocol.getQualifiedName())
                    .relate(QUERIES_ACTOR, actor.getQualifiedName());
        }
    }

    private void resolveRoutesAnnotation(final TypeElement autoDispatchClass,
                                         final CodeGenerationParameter autoDispatchParameter) {
        final String uriRoot = autoDispatchParameter.relatedParameterValueOf(URI_ROOT);
        final Predicate<Element> filter = element -> element instanceof ExecutableElement;
        final Function<Element, ExecutableElement> mapper = element -> (ExecutableElement) element;

        autoDispatchClass.getEnclosedElements().stream().filter(filter).map(mapper).forEach(method -> {
            final Route routeAnnotation =
                    method.getAnnotation(Route.class);

            if(routeAnnotation != null) {
                final CodeGenerationParameter routeParameter =
                    CodeGenerationParameter.of(ROUTE_SIGNATURE, buildRouteSignature(method));

                routeParameter.relate(ROUTE_HANDLER, routeAnnotation.handler())
                        .relate(ROUTE_METHOD, routeAnnotation.method())
                        .relate(CUSTOM_ROUTE, method.getModifiers().contains(DEFAULT))
                        .relate(ROUTE_PATH, RoutePath.resolve(uriRoot, routeAnnotation.path()));

                resolveVariablesAnnotation(method, routeParameter);
                resolveResponseAnnotation(method, routeParameter);
                autoDispatchParameter.relate(routeParameter);
            }
        });
    }

    private String buildRouteSignature(final ExecutableElement method) {
        final String signatureParameters =
                method.getParameters().stream().map(param -> {
                    final Name paramType =
                            environment.getTypeUtils()
                                    .asElement(param.asType())
                                    .getSimpleName();

                    return paramType + " " + param.getSimpleName();
                }).collect(Collectors.joining(", "));

        return String.format(ROUTE_SIGNATURE_PATTERN, method.getSimpleName(), signatureParameters);
    }

    private void resolveVariablesAnnotation(final ExecutableElement method,
                                            final CodeGenerationParameter routeParameter) {
        method.getParameters().forEach(methodVariable -> {
            resolveIdAnnotation(methodVariable, routeParameter);
            resolveBodyAnnotation(methodVariable, routeParameter);
        });
    }

    private void resolveIdAnnotation(final VariableElement methodVariable,
                                     final CodeGenerationParameter routeParameter) {
        final Id idAnnotation = methodVariable.getAnnotation(Id.class);

        if(idAnnotation != null) {
            final TypeElement idtype =
                    (TypeElement) environment.getTypeUtils()
                            .asElement(methodVariable.asType());

            routeParameter.relate(ID, methodVariable.getSimpleName())
                    .relate(ID_TYPE, idtype.getQualifiedName());
        }
    }

    private void resolveBodyAnnotation(final VariableElement methodVariable,
                                       final CodeGenerationParameter routeParameter) {
        final Body bodyAnnotation = methodVariable.getAnnotation(Body.class);

        if(bodyAnnotation != null) {
            final TypeElement bodyType =
                    (TypeElement) environment.getTypeUtils()
                            .asElement(methodVariable.asType());

            routeParameter.relate(BODY, methodVariable.getSimpleName())
                    .relate(BODY_TYPE, bodyType.getQualifiedName());
        }
    }

    private void resolveResponseAnnotation(final ExecutableElement method,
                                           final CodeGenerationParameter routeParameter) {
        final ResponseAdapter responseAdapterAnnotation = method.getAnnotation(ResponseAdapter.class);

        if(responseAdapterAnnotation != null) {
            routeParameter.relate(RESPONSE_DATA, responseAdapterAnnotation.value());
        }
    }

}
