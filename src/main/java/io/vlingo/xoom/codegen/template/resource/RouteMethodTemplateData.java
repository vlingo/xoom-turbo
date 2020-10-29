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
import io.vlingo.xoom.codegen.template.autodispatch.HandlerInvocationResolver;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.CUSTOM_ROUTE;
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
                parameter -> !parameter.relatedParameterValueOf(CUSTOM_ROUTE, Boolean::valueOf);

        final Function<CodeGenerationParameter, RouteMethodTemplateData> mapper =
                routeSignatureParameter -> new RouteMethodTemplateData(mainParameter,
                        routeSignatureParameter, parentParameters);

        return mainParameter.retrieveAll(Label.ROUTE_SIGNATURE)
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
        final String routeHandlerInvocation =
                HandlerInvocationResolver.resolveRouteHandlerInvocation(mainParameter, routeSignatureParameter);

        final String adapterHandlerInvocation =
                HandlerInvocationResolver.resolveAdapterHandlerInvocation(mainParameter, routeSignatureParameter);

        this.parameters =
                TemplateParameters.with(ROUTE_SIGNATURE, routeSignatureParameter.value)
                        .and(MODEL_ATTRIBUTE, resolveAttributeName(mainParameter, Label.MODEL_PROTOCOL))
                        .and(ROUTE_METHOD, routeSignatureParameter.relatedParameterValueOf(Label.ROUTE_METHOD))
                        .and(REQUIRE_ENTITY_LOADING, routeSignatureParameter.hasAny(Label.ID))
                        .and(ADAPTER_HANDLER_INVOCATION, adapterHandlerInvocation)
                        .and(ROUTE_HANDLER_INVOCATION, routeHandlerInvocation)
                        .and(ID_NAME, resolveIdName(routeSignatureParameter));

        parentParameters.addImports(resolveImports(mainParameter, routeSignatureParameter));
    }

    private Set<String> resolveImports(final CodeGenerationParameter mainParameter,
                                       final CodeGenerationParameter routeSignatureParameter) {
        return Stream.of(retrieveIdTypeQualifiedName(routeSignatureParameter),
                routeSignatureParameter.relatedParameterValueOf(Label.BODY_TYPE),
                mainParameter.relatedParameterValueOf(Label.HANDLERS_CONFIG_NAME),
                mainParameter.relatedParameterValueOf(Label.MODEL_PROTOCOL),
                mainParameter.relatedParameterValueOf(Label.MODEL_ACTOR),
                mainParameter.relatedParameterValueOf(Label.MODEL_DATA))
                .filter(qualifiedName -> !qualifiedName.isEmpty())
                .collect(Collectors.toSet());
    }

    private String resolveIdName(final CodeGenerationParameter routeSignatureParameter) {
        if(!routeSignatureParameter.hasAny(Label.ID)) {
            return DEFAULT_ID_NAME;
        }
        return routeSignatureParameter.relatedParameterValueOf(Label.ID);
    }

    private String retrieveIdTypeQualifiedName(final CodeGenerationParameter routeSignatureParameter) {
        final String idType = routeSignatureParameter.relatedParameterValueOf(Label.ID_TYPE);
        return idType.contains(".") ? "" : idType;
    }

    private String resolveAttributeName(final CodeGenerationParameter mainParameter,
                                        final Label protocolLabel) {
        if(!mainParameter.hasAny(protocolLabel)) {
            return "";
        }
        final String qualifiedName = mainParameter.relatedParameterValueOf(protocolLabel);
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
