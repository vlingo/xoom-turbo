// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.autodispatch;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.*;

public class RouteDeclarationParameter {

    private final String path;
    private final String bodyType;
    private final String handlerName;
    private final String builderMethod;
    private final List<String> parameterTypes = new ArrayList<>();

    public static List<RouteDeclarationParameter> from(final CodeGenerationParameter autoDispatchParameter) {
        return autoDispatchParameter.retrieveAll(Label.ROUTE_SIGNATURE)
                .map(RouteDeclarationParameter::new).collect(Collectors.toList());
    }

    private RouteDeclarationParameter(final CodeGenerationParameter routeSignatureParameter) {
        this.handlerName = resolveHandlerName(routeSignatureParameter);
        this.path = routeSignatureParameter.relatedParameterValueOf(ROUTE_PATH);
        this.bodyType = routeSignatureParameter.relatedParameterValueOf(BODY_TYPE);
        this.builderMethod = routeSignatureParameter.relatedParameterValueOf(ROUTE_METHOD);
        this.parameterTypes.addAll(resolveParameterTypes(routeSignatureParameter));
    }

    private String resolveHandlerName(final CodeGenerationParameter routeSignatureParameter) {
        final String handlerName =
                routeSignatureParameter.relatedParameterValueOf(ROUTE_HANDLER);

        return handlerName.substring(0, handlerName.indexOf("("));
    }

    private List<String> resolveParameterTypes(final CodeGenerationParameter routeSignatureParameter) {
        final String bodyParameterName =
                routeSignatureParameter.relatedParameterValueOf(BODY);

        final String handlerName =
                routeSignatureParameter.relatedParameterValueOf(ROUTE_HANDLER);

        final String parameters =
                handlerName.substring(handlerName.indexOf("(") + 1, handlerName.lastIndexOf(")"));

        if(parameters.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return Stream.of(parameters.split(","))
                .map(parameter -> parameter.replaceAll("final", "").trim())
                .filter(parameter -> !parameter.endsWith(" " + bodyParameterName))
                .map(parameter -> parameter.substring(0, parameter.indexOf(" ")))
                .collect(Collectors.toList());
    }

    public String getPath() {
        return path;
    }

    public String getBodyType() {
        return bodyType;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public String getBuilderMethod() {
        return builderMethod.toLowerCase();
    }

    public List<String> getParameterTypes() {
        return parameterTypes;
    }

}
