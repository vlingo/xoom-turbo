// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.autodispatch;

import io.vlingo.xoom.codegen.content.ClassFormatter;
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

import static io.vlingo.xoom.codegen.parameter.Label.CUSTOM_ROUTE;
import static io.vlingo.xoom.codegen.template.TemplateParameter.*;

public class RouteMethodTemplateData extends TemplateData {

    private final TemplateParameters parameters;

    public static List<TemplateData> from(final CodeGenerationParameter autoDispatchParameter,
                                          final TemplateParameters parentParameters) {
        final Predicate<CodeGenerationParameter> filter =
                parameter -> !Boolean.valueOf(parameter.relatedParameterValueOf(CUSTOM_ROUTE));

        final Function<CodeGenerationParameter, RouteMethodTemplateData> mapper =
                routeSignatureParameter -> new RouteMethodTemplateData(autoDispatchParameter,
                        routeSignatureParameter, parentParameters);

        return autoDispatchParameter.retrieveAll(Label.ROUTE_SIGNATURE)
                .filter(filter).map(mapper).collect(Collectors.toList());
    }

    private RouteMethodTemplateData(final CodeGenerationParameter autoDispatchParameter,
                                    final CodeGenerationParameter routeSignatureParameter,
                                    final TemplateParameters parentParameters) {
        final String routeHandlerInvocation =
                HandlerInvocationResolver.resolve(Label.ROUTE_HANDLER_INVOCATION,
                        autoDispatchParameter, routeSignatureParameter);

        final String adapterHandlerInvocation =
                HandlerInvocationResolver.resolve(Label.ADAPTER_HANDLER_INVOCATION,
                        autoDispatchParameter, routeSignatureParameter);

        this.parameters =
                TemplateParameters.with(ROUTE_SIGNATURE, routeSignatureParameter.value)
                        .and(MODEL_ATTRIBUTE, resolveAttributeName(autoDispatchParameter, Label.MODEL_PROTOCOL))
                        .and(ROUTE_METHOD, routeSignatureParameter.relatedParameterValueOf(Label.ROUTE_METHOD))
                        .and(ID_NAME, routeSignatureParameter.relatedParameterValueOf(Label.ID))
                        .and(REQUIRE_ENTITY_LOADING, routeSignatureParameter.hasAny(Label.ID))
                        .and(ROUTE_HANDLER_INVOCATION, routeHandlerInvocation)
                        .and(ADAPTER_HANDLER_INVOCATION, adapterHandlerInvocation);

        parentParameters.addImports(resolveImports(autoDispatchParameter, routeSignatureParameter));
    }

    private Set<String> resolveImports(final CodeGenerationParameter autoDispatchParameter,
                                       final CodeGenerationParameter routeSignatureParameter) {
        return Stream.of(routeSignatureParameter.relatedParameterValueOf(Label.ID_TYPE),
                autoDispatchParameter.relatedParameterValueOf(Label.HANDLERS_CONFIG_NAME),
                routeSignatureParameter.relatedParameterValueOf(Label.BODY_TYPE),
                autoDispatchParameter.relatedParameterValueOf(Label.MODEL_DATA))
                .filter(qualifiedName -> !qualifiedName.isEmpty())
                .collect(Collectors.toSet());
    }

    private String resolveAttributeName(final CodeGenerationParameter autoDispatchParameter,
                                        final Label protocolLabel) {
        if(!autoDispatchParameter.hasAny(protocolLabel)) {
            return "";
        }
        final String qualifiedName = autoDispatchParameter.relatedParameterValueOf(protocolLabel);
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
