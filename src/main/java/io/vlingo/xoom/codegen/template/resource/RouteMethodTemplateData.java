// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.resource;

import io.vlingo.xoom.codegen.content.ClassFormatter;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.INTERNAL_ROUTE_HANDLER;
import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE_PROTOCOL;

public class RouteMethodTemplateData extends TemplateData {

    private static final String DEFAULT_ID_NAME = "id";

    private final TemplateParameters parameters;

    public static List<TemplateData> from(final CodeGenerationParameter aggregateParameter,
                                          final TemplateParameters parentParameters,
                                          final List<Content> contents) {
        inferModelParameters(aggregateParameter, contents);
        return from(aggregateParameter, parentParameters);
    }

    public static List<TemplateData> from(final CodeGenerationParameter mainParameter,
                                          final TemplateParameters parentParameters) {
        final Predicate<CodeGenerationParameter> filter =
                parameter -> !parameter.retrieveRelatedValue(INTERNAL_ROUTE_HANDLER, Boolean::valueOf);

        final Function<CodeGenerationParameter, RouteMethodTemplateData> mapper =
                routeSignatureParameter -> new RouteMethodTemplateData(mainParameter,
                        routeSignatureParameter, parentParameters);

        return mainParameter.retrieveAllRelated(Label.ROUTE_SIGNATURE)
                .filter(filter).map(mapper).collect(Collectors.toList());
    }

    private static void inferModelParameters(final CodeGenerationParameter aggregateParameter,
                                             final List<Content> contents) {
        final String modelActor = AGGREGATE.resolveClassname(aggregateParameter.value);

        final String modelProtocolQualifiedName =
                ContentQuery.findFullyQualifiedClassName(AGGREGATE_PROTOCOL, aggregateParameter.value, contents);

        final String modelActorQualifiedName =
                ContentQuery.findFullyQualifiedClassName(AGGREGATE, modelActor, contents);

        aggregateParameter.relate(Label.MODEL_PROTOCOL, modelProtocolQualifiedName)
                .relate(Label.MODEL_ACTOR, modelActorQualifiedName);
    }

    private RouteMethodTemplateData(final CodeGenerationParameter mainParameter,
                                    final CodeGenerationParameter routeSignatureParameter,
                                    final TemplateParameters parentParameters) {
        final HandlerInvocationResolver invocationResolver = HandlerInvocationResolver.with(mainParameter);

        final String routeHandlerInvocation =
                invocationResolver.resolveRouteHandlerInvocation(mainParameter, routeSignatureParameter);

        final String adapterHandlerInvocation =
                invocationResolver.resolveAdapterHandlerInvocation(mainParameter, routeSignatureParameter);

        this.parameters =
                TemplateParameters.with(ROUTE_SIGNATURE, RouteDetail.resolveMethodSignature(routeSignatureParameter))
                        .and(MODEL_ATTRIBUTE, resolveModelAttributeName(mainParameter, Label.MODEL_PROTOCOL))
                        .and(ROUTE_METHOD, routeSignatureParameter.retrieveRelatedValue(Label.ROUTE_METHOD))
                        .and(REQUIRE_ENTITY_LOADING, resolveEntityLoading(routeSignatureParameter))
                        .and(ADAPTER_HANDLER_INVOCATION, adapterHandlerInvocation)
                        .and(ROUTE_HANDLER_INVOCATION, routeHandlerInvocation)
                        .and(ID_NAME, resolveIdName(routeSignatureParameter));

        parentParameters.addImports(resolveImports(mainParameter, routeSignatureParameter));
    }

    private Set<String> resolveImports(final CodeGenerationParameter mainParameter,
                                       final CodeGenerationParameter routeSignatureParameter) {
        return Stream.of(retrieveIdTypeQualifiedName(routeSignatureParameter),
                routeSignatureParameter.retrieveRelatedValue(Label.BODY_TYPE),
                mainParameter.retrieveRelatedValue(Label.HANDLERS_CONFIG_NAME),
                mainParameter.retrieveRelatedValue(Label.MODEL_PROTOCOL),
                mainParameter.retrieveRelatedValue(Label.MODEL_ACTOR),
                mainParameter.retrieveRelatedValue(Label.MODEL_DATA))
                .filter(qualifiedName -> !qualifiedName.isEmpty())
                .collect(Collectors.toSet());
    }

    private Boolean resolveEntityLoading(final CodeGenerationParameter routeSignatureParameter) {
        return routeSignatureParameter.hasAny(Label.ID) ||
                (routeSignatureParameter.hasAny(Label.REQUIRE_ENTITY_LOADING) &&
                        routeSignatureParameter.retrieveRelatedValue(Label.REQUIRE_ENTITY_LOADING, Boolean::valueOf));
    }

    private String resolveIdName(final CodeGenerationParameter routeSignatureParameter) {
        if(!routeSignatureParameter.hasAny(Label.ID)) {
            return DEFAULT_ID_NAME;
        }
        return routeSignatureParameter.retrieveRelatedValue(Label.ID);
    }

    private String retrieveIdTypeQualifiedName(final CodeGenerationParameter routeSignatureParameter) {
        final String idType = routeSignatureParameter.retrieveRelatedValue(Label.ID_TYPE);
        return idType.contains(".") ? "" : idType;
    }

    private String resolveModelAttributeName(final CodeGenerationParameter mainParameter,
                                             final Label protocolLabel) {
        if (mainParameter.isLabeled(Label.AGGREGATE)) {
            return ClassFormatter.simpleNameToAttribute(mainParameter.value);
        }
        final String qualifiedName = mainParameter.retrieveRelatedValue(protocolLabel);
        return ClassFormatter.qualifiedNameToAttribute(qualifiedName);
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public TemplateStandard standard() {
        return TemplateStandard.ROUTE_METHOD;
    }

}
