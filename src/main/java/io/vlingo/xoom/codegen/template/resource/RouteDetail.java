// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.resource;

import io.vlingo.http.Method;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;

import java.util.Arrays;
import java.util.List;

import static io.vlingo.http.Method.*;
import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.ENTITY_DATA;

public class RouteDetail {

    private static final List<Method> BODY_SUPPORTED_HTTP_METHODS = Arrays.asList(POST, PUT, PATCH);

    public static String resolveBodyType(final CodeGenerationParameter route) {
        final Method httpMethod = route.relatedParameterValueOf(ROUTE_METHOD, Method::valueOf);

        if(!BODY_SUPPORTED_HTTP_METHODS.contains(httpMethod)) {
            return "";
        }

        if(route.parent().isLabeled(AGGREGATE)) {
            return route.parent().retrieveAll(AGGREGATE_METHOD)
                    .filter(method -> method.value.equals(route.value))
                    .map(method -> ENTITY_DATA.resolveClassname(method.parent(AGGREGATE).value))
                    .findFirst().get();
        }

        return route.relatedParameterValueOf(BODY_TYPE);
    }

    public static boolean requireEntityLoad(final CodeGenerationParameter aggregateParameter) {
        return aggregateParameter.retrieveAll(ROUTE_SIGNATURE)
                .filter(route -> route.hasAny(REQUIRE_ENTITY_LOADING))
                .anyMatch(route -> route.relatedParameterValueOf(REQUIRE_ENTITY_LOADING, Boolean::valueOf));
    }

    public static CodeGenerationParameter defaultQueryRouteParameter() {
        return CodeGenerationParameter.of(ROUTE_SIGNATURE, "queryAll")
                .relate(ROUTE_METHOD, GET).relate(READ_ONLY, "true");
    }
}
