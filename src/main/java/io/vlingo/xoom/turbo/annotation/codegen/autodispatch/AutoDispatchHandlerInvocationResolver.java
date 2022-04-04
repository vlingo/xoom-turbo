// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.annotation.codegen.autodispatch;

import io.vlingo.xoom.codegen.content.CodeElementFormatter;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.ParameterLabel;
import io.vlingo.xoom.http.Method;
import io.vlingo.xoom.turbo.ComponentRegistry;
import io.vlingo.xoom.turbo.annotation.codegen.Label;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoDispatchHandlerInvocationResolver {

  private static final String QUERIES_PARAMETER = "$queries";
  private static final String DEFAULT_ADAPTER_PARAMETER = "state";
  private static final String HANDLER_INVOCATION_PATTERN = "%s.%s";
  private static final String DEFAULT_FACTORY_METHOD_PARAMETER = "$stage";
  private static final String HANDLER_INVOCATION_WITH_DEFAULT_PARAMS_PATTERN = "%s.%s(%s)";

  private final CodeElementFormatter codeElementFormatter;

  public AutoDispatchHandlerInvocationResolver() {
    this.codeElementFormatter = ComponentRegistry.withType(CodeElementFormatter.class);
  }

  public String resolveRouteHandlerInvocation(final CodeGenerationParameter parentParameter,
                                              final CodeGenerationParameter routeSignatureParameter) {
    final Method httpMethod =
            routeSignatureParameter.retrieveRelatedValue(Label.ROUTE_METHOD, Method::from);

    final String compositeIdParameter = compositeIdParameterFrom(routeSignatureParameter);

    String queriesParameters = QUERIES_PARAMETER;
    if(!compositeIdParameter.isEmpty())
      queriesParameters += String.format(", %s", compositeIdParameter);

    final String defaultParameter = httpMethod.isGET() ? queriesParameters : DEFAULT_FACTORY_METHOD_PARAMETER;

    return resolve(Label.ROUTE_HANDLER_INVOCATION, Label.USE_CUSTOM_ROUTE_HANDLER_PARAM, defaultParameter, parentParameter, routeSignatureParameter);
  }

  private static String compositeIdParameterFrom(CodeGenerationParameter routeSignature) {
    String routePath = routeSignature.retrieveRelatedValue(Label.ROUTE_PATH);
    if(!routePath.startsWith(routeSignature.parent().retrieveRelatedValue(Label.URI_ROOT))) {
      routePath = routeSignature.parent().retrieveRelatedValue(Label.URI_ROOT) + routePath;
    }
    final String compositeId = String.join(", ", extractCompositeIdFrom(routePath));

    return !compositeId.isEmpty()? compositeId : "";
  }

  private static List<String> extractCompositeIdFrom(String routePath) {
    final List<String> result = new ArrayList<>();

    final String regex = "\\{(.*?)\\}";

    final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
    final Matcher matcher = pattern.matcher(routePath);

    while (matcher.find()) {
      result.add(matcher.group(1));
    }

    return result;
  }

  public String resolveAdapterHandlerInvocation(final CodeGenerationParameter parentParameter,
                                                final CodeGenerationParameter routeSignatureParameter) {
    return resolve(Label.ADAPTER_HANDLER_INVOCATION, Label.USE_CUSTOM_ADAPTER_HANDLER_PARAM, DEFAULT_ADAPTER_PARAMETER, parentParameter, routeSignatureParameter);
  }

  private String resolve(final ParameterLabel invocationLabel,
                         final ParameterLabel customParamsLabel,
                         final String defaultParameter,
                         final CodeGenerationParameter parentParameter,
                         final CodeGenerationParameter routeSignatureParameter) {
    if (!routeSignatureParameter.hasAny(customParamsLabel)) {
      return "";
    }

    final String handlersConfigQualifiedName =
            parentParameter.retrieveRelatedValue(Label.HANDLERS_CONFIG_NAME);

    final String handlersConfigClassName =
            codeElementFormatter.simpleNameOf(handlersConfigQualifiedName);

    final String invocation = routeSignatureParameter.retrieveRelatedValue(invocationLabel);

    if (routeSignatureParameter.retrieveRelatedValue(customParamsLabel, Boolean::valueOf)) {
      return String.format(HANDLER_INVOCATION_PATTERN, handlersConfigClassName, invocation);
    }

    return String.format(HANDLER_INVOCATION_WITH_DEFAULT_PARAMS_PATTERN, handlersConfigClassName, invocation, defaultParameter);
  }

}
