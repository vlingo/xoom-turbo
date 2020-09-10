// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.autodispatch;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import java.beans.Introspector;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateParameter.MODEL_PROTOCOL;
import static io.vlingo.xoom.codegen.template.TemplateParameter.ROUTE_HANDLER;
import static io.vlingo.xoom.codegen.template.TemplateParameter.ROUTE_METHOD;
import static io.vlingo.xoom.codegen.template.TemplateParameter.ROUTE_SIGNATURE;
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
        this.parameters =
                TemplateParameters.with(MODEL_PROTOCOL, autoDispatchParameter.relatedParameterValueOf(Label.MODEL_PROTOCOL))
                        .and(QUERIES_ATTRIBUTE, resolveQueriesAttribute(autoDispatchParameter))
                        .and(ADAPTER_INVOCATION, autoDispatchParameter.relatedParameterValueOf(Label.RESPONSE_DATA))
                        .and(ROUTE_METHOD, routeSignatureParameter.relatedParameterValueOf(Label.ROUTE_METHOD))
                        .and(ROUTE_HANDLER, routeSignatureParameter.relatedParameterValueOf(Label.ROUTE_HANDLER))
                        .and(ID_NAME, routeSignatureParameter.relatedParameterValueOf(Label.ID))
                        .and(ROUTE_SIGNATURE, routeSignatureParameter.value);

        parentParameters.addImports(resolveImports(autoDispatchParameter, routeSignatureParameter));
    }

    private String resolveQueriesAttribute(final CodeGenerationParameter autoDispatchParameter) {
        if(!autoDispatchParameter.hasAny(QUERIES_PROTOCOL)) {
            return "";
        }

        final String qualifiedName =
                autoDispatchParameter.relatedParameterValueOf(QUERIES_PROTOCOL);

        final String className =
                qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);

        return Introspector.decapitalize(className);
    }

    private Set<String> resolveImports(final CodeGenerationParameter autoDispatchParameter,
                                       final CodeGenerationParameter routeSignatureParameter) {
        return Stream.of(routeSignatureParameter.relatedParameterValueOf(ID_TYPE),
                routeSignatureParameter.relatedParameterValueOf(BODY_TYPE),
                autoDispatchParameter.relatedParameterValueOf(MODEL_DATA))
                .filter(qualifiedName -> !qualifiedName.isEmpty())
                .collect(Collectors.toSet());
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
