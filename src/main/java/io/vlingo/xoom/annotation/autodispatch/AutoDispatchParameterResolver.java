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
import io.vlingo.xoom.codegen.template.resource.PathFormatter;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static io.vlingo.xoom.codegen.parameter.Label.*;
import static javax.lang.model.element.Modifier.DEFAULT;

public class AutoDispatchParameterResolver {

    private static final String SIGNATURE_PATTERN = "%s(%s)";

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

        this.autoDispatchResourceClasses.stream()
                .map(this::resolveAutoDispatchAnnotation).forEach(parameters::add);

        return parameters;
    }

    private CodeGenerationParameter resolveAutoDispatchAnnotation(final TypeElement autoDispatchClass) {
        final AutoDispatch autoDispatchAnnotation =
                autoDispatchClass.getAnnotation(AutoDispatch.class);

        final TypeElement handlersConfigClass =
                typeRetriever.from(autoDispatchAnnotation, AutoDispatch::handlers);

        final CodeGenerationParameter autoDispatchParameter =
                CodeGenerationParameter.of(AUTO_DISPATCH_NAME, autoDispatchClass.getQualifiedName())
                        .relate(HANDLERS_CONFIG_NAME, handlersConfigClass.getQualifiedName())
                        .relate(URI_ROOT, resolveRootURI(autoDispatchAnnotation));

        resolveModelAnnotation(autoDispatchClass, autoDispatchParameter);
        resolveQueriesAnnotation(autoDispatchClass, autoDispatchParameter);
        resolveRoutesAnnotation(autoDispatchClass, handlersConfigClass, autoDispatchParameter);
        return autoDispatchParameter;
    }

    private void resolveModelAnnotation(final TypeElement autoDispatchClass,
                                        final CodeGenerationParameter autoDispatchParameter) {
        final Model modelAnnotation =
                autoDispatchClass.getAnnotation(Model.class);

        if(modelAnnotation != null) {
            final TypeElement protocol =
                    typeRetriever.from(modelAnnotation, Model::protocol);

            final TypeElement actor =
                    typeRetriever.from(modelAnnotation, Model::actor);

            final TypeElement data =
                    typeRetriever.from(modelAnnotation, Model::data);

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
                    typeRetriever.from(queriesAnnotation, Queries::protocol);

            final TypeElement actor =
                    typeRetriever.from(queriesAnnotation, Queries::actor);

            autoDispatchParameter.relate(QUERIES_PROTOCOL, protocol.getQualifiedName())
                    .relate(QUERIES_ACTOR, actor.getQualifiedName());
        }
    }

    private void resolveRoutesAnnotation(final TypeElement autoDispatchClass,
                                         final TypeElement handlersConfigClass,
                                         final CodeGenerationParameter autoDispatchParameter) {
        final String uriRoot = autoDispatchParameter.retrieveRelatedValue(URI_ROOT);
        final Predicate<Element> filter = element -> element instanceof ExecutableElement;
        final Function<Element, ExecutableElement> mapper = element -> (ExecutableElement) element;
        final HandlerResolver handlerResolver = HandlerResolver.with(handlersConfigClass, environment);

        autoDispatchClass.getEnclosedElements().stream().filter(filter).map(mapper).forEach(method -> {
            final Route routeAnnotation =
                    method.getAnnotation(Route.class);

            if(routeAnnotation != null) {
                final Boolean internalRouteHandler = method.getModifiers().contains(DEFAULT);

                final CodeGenerationParameter routeParameter =
                    CodeGenerationParameter.of(ROUTE_SIGNATURE, buildMethodSignature(method));

                routeParameter.relate(ROUTE_METHOD, routeAnnotation.method())
                        .relate(INTERNAL_ROUTE_HANDLER, internalRouteHandler)
                        .relate(ROUTE_PATH, PathFormatter.formatAbsoluteRoutePath(uriRoot, routeAnnotation.path()));

                if(!internalRouteHandler) {
                    final HandlerInvocation handlerInvocation = handlerResolver.find(routeAnnotation.handler());

                    routeParameter.relate(ROUTE_HANDLER_INVOCATION, handlerInvocation.invocation)
                            .relate(USE_CUSTOM_ROUTE_HANDLER_PARAM, handlerInvocation.hasCustomParamNames());
                }

                resolveVariablesAnnotation(method, routeParameter);
                resolveResponseAnnotation(method, routeParameter, handlerResolver);
                autoDispatchParameter.relate(routeParameter);
            }
        });
    }

    private String buildMethodSignature(final ExecutableElement method) {
        final String signatureParameters =
                method.getParameters().stream().map(param ->
                        param.asType() + " " + param.getSimpleName())
                        .collect(Collectors.joining(", "));

        return String.format(SIGNATURE_PATTERN, method.getSimpleName(), signatureParameters);
    }

    private void resolveVariablesAnnotation(final ExecutableElement method,
                                            final CodeGenerationParameter routeParameter) {
        method.getParameters().forEach(methodArguments -> {
            resolveIdAnnotation(methodArguments, routeParameter);
            resolveBodyAnnotation(methodArguments, routeParameter);
            resolveOtherParameters(methodArguments, routeParameter);
        });
    }

    private void resolveOtherParameters(final VariableElement methodArgument,
                                        final CodeGenerationParameter routeParameter) {
        if(methodArgument.getAnnotation(Id.class) == null &&
                methodArgument.getAnnotation(Body.class) == null) {
            routeParameter.relate(METHOD_PARAMETER, methodArgument.getSimpleName());
        }
    }

    private void resolveIdAnnotation(final VariableElement methodArgument,
                                     final CodeGenerationParameter routeParameter) {
        final Id idAnnotation = methodArgument.getAnnotation(Id.class);

        if(idAnnotation != null) {
            routeParameter.relate(ID, methodArgument.getSimpleName())
                    .relate(ID_TYPE, retriedIdTypeQualifiedName(methodArgument.asType()));
        }
    }

    private String retriedIdTypeQualifiedName(final TypeMirror idType) {
        if(idType.getKind().isPrimitive()) {
            return "";
        }

        final TypeElement idTypeElement =
                (TypeElement) environment.getTypeUtils().asElement(idType);

        return idTypeElement.getQualifiedName().toString();
    }



    private void resolveBodyAnnotation(final VariableElement methodArgument,
                                       final CodeGenerationParameter routeParameter) {
        final Body bodyAnnotation = methodArgument.getAnnotation(Body.class);

        if(bodyAnnotation != null) {
            final TypeElement bodyType =
                    (TypeElement) environment.getTypeUtils()
                            .asElement(methodArgument.asType());

            routeParameter.relate(BODY, methodArgument.getSimpleName())
                    .relate(BODY_TYPE, bodyType.getQualifiedName());
        }
    }

    private void resolveResponseAnnotation(final ExecutableElement method,
                                           final CodeGenerationParameter routeParameter,
                                           final HandlerResolver handlerResolver) {
        final ResponseAdapter responseAdapterAnnotation = method.getAnnotation(ResponseAdapter.class);

        if(responseAdapterAnnotation != null) {
            final HandlerInvocation handlerInvocation =
                    handlerResolver.find(responseAdapterAnnotation.handler());

            routeParameter.relate(USE_ADAPTER, true)
                    .relate(ADAPTER_HANDLER_INVOCATION, handlerInvocation.invocation)
                    .relate(USE_CUSTOM_ADAPTER_HANDLER_PARAM, handlerInvocation.hasCustomParamNames());
        } else {
            routeParameter.relate(USE_ADAPTER, false);
        }
    }

    private String resolveRootURI(final AutoDispatch autoDispatch) {
        final String rootURI = autoDispatch.path();
        return rootURI.endsWith("/") ? rootURI : rootURI + "/";
    }
}
