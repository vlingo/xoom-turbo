// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.codegen.autodispatch;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.http.Method;
import io.vlingo.xoom.turbo.annotation.codegen.AnnotationBasedTemplateStandard;
import io.vlingo.xoom.turbo.annotation.codegen.Label;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.http.Method.*;
import static io.vlingo.xoom.turbo.annotation.codegen.Label.*;

public class RouteDetail {

  private static final String BODY_DEFAULT_NAME = "data";
  private static final String METHOD_PARAMETER_PATTERN = "final %s %s";
  private static final String METHOD_SIGNATURE_PATTERN = "%s(%s)";
  private static final List<Method> BODY_SUPPORTED_HTTP_METHODS = Arrays.asList(POST, PUT, PATCH);
  private static final String COMPOSITE_ID_TYPE = "String";

  public static String resolveBodyName(final CodeGenerationParameter route) {
    final Method httpMethod = route.retrieveRelatedValue(ROUTE_METHOD, Method::from);

    if (!BODY_SUPPORTED_HTTP_METHODS.contains(httpMethod)) {
      return "";
    }

    if (route.hasAny(BODY)) {
      return route.retrieveRelatedValue(BODY);
    }

    return BODY_DEFAULT_NAME;
  }

  public static String resolveBodyType(final CodeGenerationParameter route) {
    final Method httpMethod = route.retrieveRelatedValue(ROUTE_METHOD, Method::from);

    if (!BODY_SUPPORTED_HTTP_METHODS.contains(httpMethod)) {
      return "";
    }

    if (route.parent().isLabeled(AGGREGATE)) {
      return AnnotationBasedTemplateStandard.DATA_OBJECT.resolveClassname(route.parent(AGGREGATE).value);
    }

    return route.retrieveRelatedValue(BODY_TYPE);
  }

  public static String resolveMethodSignature(final CodeGenerationParameter routeSignature) {
    if (hasValidMethodSignature(routeSignature.value)) {
      return routeSignature.value;
    }

    if (routeSignature.retrieveRelatedValue(Label.ROUTE_METHOD, Method::from).isGET()) {
      final Stream<CodeGenerationParameter> parameters =
              routeSignature.retrieveAllRelated(METHOD_PARAMETER);

      final String arguments = parameters.map(field -> {
        final String fieldType = FieldDetail.typeOf(field.parent(Label.AGGREGATE), field.value);
        return String.format(METHOD_PARAMETER_PATTERN, fieldType, field.value);
      }).collect(Collectors.joining(", "));

      final String compositeIdParameter = String.join(",", compositeIdParameterFrom(routeSignature));

      final String params = formatParameters(Stream.of(compositeIdParameter, arguments));
      return String.format(METHOD_SIGNATURE_PATTERN, routeSignature.value, params);
    }

    return resolveMethodSignatureWithParams(routeSignature);
  }

  private static String resolveMethodSignatureWithParams(final CodeGenerationParameter routeSignature) {
    final String idParameter =
            routeSignature.retrieveRelatedValue(REQUIRE_ENTITY_LOADING, Boolean::valueOf) ?
                    String.format(METHOD_PARAMETER_PATTERN, "String", "id") : "";

    final String compositeIdParameter = String.join(",", compositeIdParameterFrom(routeSignature));

    final CodeGenerationParameter method = AggregateDetail.methodWithName(routeSignature.parent(), routeSignature.value);
    final String dataClassname = AnnotationBasedTemplateStandard.DATA_OBJECT.resolveClassname(routeSignature.parent().value);
    final String dataParameterDeclaration = String.format(METHOD_PARAMETER_PATTERN, dataClassname, "data");
    final String dataParameter = method.hasAny(METHOD_PARAMETER) ? dataParameterDeclaration : "";

    final String parameters = formatParameters(Stream.of(compositeIdParameter, idParameter, dataParameter));
    return String.format(METHOD_SIGNATURE_PATTERN, routeSignature.value, parameters);
  }

  private static List<String> compositeIdParameterFrom(CodeGenerationParameter routeSignature) {
    String routePath = resolveRoutePathFrom(routeSignature);
    final List<String> compositeIds = extractCompositeIdFrom(routePath);

    return compositeIds.stream()
            .map(compositeId -> String.format(METHOD_PARAMETER_PATTERN, COMPOSITE_ID_TYPE, compositeId))
            .collect(Collectors.toList());
  }

  public static List<String> extractCompositeIdFrom(String routePath) {
    return extractAllPathVariablesFrom(routePath)
        .stream().filter(pathVar -> !pathVar.equals("id"))
        .collect(Collectors.toList());
  }

  public static String resolveCompositeIdFields(CodeGenerationParameter routeSignature) {
    String routePath = resolveRoutePathFrom(routeSignature);
    final String compositeId = String.join(",", extractCompositeIdFrom(routePath));

    return !compositeId.isEmpty() && !compositeId.equals("id")? compositeId + ", " : "";
  }

  public static String resolveCompositeIdParameterFrom(CodeGenerationParameter routeSignature) {
    String routePath = resolveRoutePathFrom(routeSignature);
    final String compositeId = String.join(", ", extractAllPathVariablesFrom(routePath));

    return !compositeId.isEmpty()? compositeId : "";
  }

  private static List<String> extractAllPathVariablesFrom(String routePath) {
    final List<String> result = new ArrayList<>();

    final String regex = "\\{(.*?)\\}";

    final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
    final Matcher matcher = pattern.matcher(routePath);

    while (matcher.find()) {
      result.add(matcher.group(1));
    }

    return result;
  }

  private static String resolveRoutePathFrom(CodeGenerationParameter routeSignature) {
    String routePath = routeSignature.retrieveRelatedValue(Label.ROUTE_PATH);
    if(!routePath.startsWith(routeSignature.parent().retrieveRelatedValue(Label.URI_ROOT))) {
      routePath = routeSignature.parent().retrieveRelatedValue(Label.URI_ROOT) + routePath;
    }
    return routePath;
  }

  private static String formatParameters(Stream<String> arguments) {
    return arguments
            .distinct()
            .filter(param -> !param.isEmpty())
            .collect(Collectors.joining(", "));
  }

  private static boolean hasValidMethodSignature(final String signature) {
    return signature.contains("(") && signature.contains(")");
  }

}
