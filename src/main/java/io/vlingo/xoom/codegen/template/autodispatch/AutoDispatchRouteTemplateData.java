// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.autodispatch;

import io.vlingo.http.Method;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.model.FieldDetail;
import io.vlingo.xoom.codegen.template.resource.PathFormatter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AUTO_DISPATCH_HANDLERS_MAPPING;
import static io.vlingo.xoom.codegen.template.TemplateStandard.DATA_OBJECT;

public class AutoDispatchRouteTemplateData extends TemplateData {

    private final TemplateParameters parameters;

    public static List<TemplateData> from(final Stream<CodeGenerationParameter> routes) {
        return routes.map(AutoDispatchRouteTemplateData::new).collect(Collectors.toList());
    }

    private AutoDispatchRouteTemplateData(final CodeGenerationParameter route) {
        final CodeGenerationParameter aggregate = route.parent(Label.AGGREGATE);
        this.parameters =
                TemplateParameters.with(RETRIEVAL_ROUTE, isRetrievalRoute(route))
                        .and(ID_TYPE, FieldDetail.typeOf(aggregate, "id"))
                        .and(ROUTE_METHOD, route.retrieveRelatedValue(Label.ROUTE_METHOD))
                        .and(ROUTE_PATH, PathFormatter.formatRelativeRoutePath(route))
                        .and(STATE_DATA_OBJECT_NAME, DATA_OBJECT.resolveClassname(aggregate.value))
                        .and(ROUTE_MAPPING_VALUE, AutoDispatchMappingValueFormatter.format(route.value))
                        .and(REQUIRE_ENTITY_LOADING, route.retrieveRelatedValue(Label.REQUIRE_ENTITY_LOADING, Boolean::valueOf))
                        .and(AUTO_DISPATCH_HANDLERS_MAPPING_NAME, AUTO_DISPATCH_HANDLERS_MAPPING.resolveClassname(aggregate.value))
                        .and(METHOD_NAME, route.value);
    }

    private boolean isRetrievalRoute(final CodeGenerationParameter route) {
        final Method method = route.retrieveRelatedValue(Label.ROUTE_METHOD, Method::from);
        return method.isGET() || method.isOPTIONS();
    }

    @Override
    public TemplateStandard standard() {
        return TemplateStandard.AUTO_DISPATCH_ROUTE;
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

}
