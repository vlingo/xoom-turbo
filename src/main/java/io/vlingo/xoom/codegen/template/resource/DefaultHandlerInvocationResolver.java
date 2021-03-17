// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.resource;

import io.vlingo.http.Method;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.model.MethodScope;
import io.vlingo.xoom.codegen.template.model.aggregate.AggregateDetail;
import io.vlingo.xoom.codegen.template.model.formatting.AggregateMethodInvocation;
import io.vlingo.xoom.codegen.template.model.formatting.Formatters;

import static io.vlingo.xoom.codegen.content.ClassFormatter.simpleNameToAttribute;
import static io.vlingo.xoom.codegen.parameter.Label.FACTORY_METHOD;
import static io.vlingo.xoom.codegen.parameter.Label.ROUTE_METHOD;
import static io.vlingo.xoom.codegen.template.TemplateStandard.DATA_OBJECT;

public class DefaultHandlerInvocationResolver implements HandlerInvocationResolver {

    private final static String COMMAND_PATTERN = "%s.%s(%s)";
    private final static String QUERY_PATTERN = HandlerInvocationResolver.QUERIES_PARAMETER + ".%s()";
    private final static String ADAPTER_PATTERN = "%s.from(state)";

    @Override
    public String resolveRouteHandlerInvocation(final CodeGenerationParameter aggregateParameter,
                                                final CodeGenerationParameter routeParameter) {
        if(routeParameter.retrieveRelatedValue(ROUTE_METHOD, Method::from).isGET()) {
            return resolveQueryMethodInvocation(routeParameter.value);
        }
        return resolveCommandMethodInvocation(aggregateParameter, routeParameter);
    }

    @Override
    public String resolveAdapterHandlerInvocation(final CodeGenerationParameter aggregateParameter,
                                                  final CodeGenerationParameter routeSignatureParameter) {
        return String.format(ADAPTER_PATTERN, DATA_OBJECT.resolveClassname(aggregateParameter.value));
    }

    private String resolveCommandMethodInvocation(final CodeGenerationParameter aggregateParameter,
                                                  final CodeGenerationParameter routeParameter) {
        final Formatters.Arguments argumentsFormat = new AggregateMethodInvocation("grid", "data");
        final CodeGenerationParameter method = AggregateDetail.methodWithName(aggregateParameter, routeParameter.value);
        final Boolean factoryMethod = method.retrieveRelatedValue(FACTORY_METHOD, Boolean::valueOf);
        final MethodScope scope = factoryMethod ? MethodScope.STATIC : MethodScope.INSTANCE;
        final String methodInvocationParameters = argumentsFormat.format(method, scope);
        final String invoker = factoryMethod ? aggregateParameter.value : simpleNameToAttribute(aggregateParameter.value);
        return String.format(COMMAND_PATTERN, invoker, method.value, methodInvocationParameters);
    }

    private String resolveQueryMethodInvocation(final String methodName) {
        return String.format(QUERY_PATTERN, methodName);
    }

}
