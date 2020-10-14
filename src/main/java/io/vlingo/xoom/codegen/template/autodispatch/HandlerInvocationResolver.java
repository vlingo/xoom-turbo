// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.autodispatch;

import io.vlingo.http.Method;
import io.vlingo.xoom.codegen.content.ClassFormatter;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;

public class HandlerInvocationResolver {

    private static final String DEFAULT_ADAPTER_PARAMETER = "state";
    private static final String HANDLER_INVOCATION_PATTERN = "%s.%s";
    private static final String DEFAULT_FACTORY_METHOD_PARAMETER = "$stage";
    private static final String HANDLER_INVOCATION_WITH_DEFAULT_PARAMS_PATTERN = "%s.%s(%s)";
    public static final String QUERIES_PARAMETER = "$queries";

    public static String resolve(final Label invocationLabel,
                                 final CodeGenerationParameter autoDispatchParameter,
                                 final CodeGenerationParameter routeSignatureParameter) {
        final String handlersConfigQualifiedName =
                autoDispatchParameter.relatedParameterValueOf(Label.HANDLERS_CONFIG_NAME);

        final String handlersConfigClassName =
                ClassFormatter.simpleNameOf(handlersConfigQualifiedName);

        final Label useCustomParams =
                invocationLabel.equals(Label.ROUTE_HANDLER_INVOCATION) ?
                        Label.USE_CUSTOM_ROUTE_HANDLER_PARAM : Label.USE_CUSTOM_ADAPTER_HANDLER_PARAM;

        if(!routeSignatureParameter.hasAny(useCustomParams)){
            return "";
        }

        final String invocation = routeSignatureParameter.relatedParameterValueOf(invocationLabel);

        if(routeSignatureParameter.relatedParameterValueOf(useCustomParams, Boolean::valueOf)) {
            return String.format(HANDLER_INVOCATION_PATTERN, handlersConfigClassName, invocation);
        }

        final String defaultParameter =
                resolveDefaultHandlerParameter(invocationLabel, autoDispatchParameter, routeSignatureParameter);

        return String.format(HANDLER_INVOCATION_WITH_DEFAULT_PARAMS_PATTERN,
                handlersConfigClassName, invocation, defaultParameter);
    }

    private static String resolveDefaultHandlerParameter(final Label invocationLabel,
                                                         final CodeGenerationParameter autoDispatchParameter,
                                                         final CodeGenerationParameter routeSignatureParameter) {
        if(invocationLabel.equals(Label.ADAPTER_HANDLER_INVOCATION)) {
            return DEFAULT_ADAPTER_PARAMETER;
        }
        if(routeSignatureParameter.relatedParameterValueOf(Label.ROUTE_METHOD, Method::from).isGET()) {
            return QUERIES_PARAMETER;
        }
        return DEFAULT_FACTORY_METHOD_PARAMETER;
    }

}
