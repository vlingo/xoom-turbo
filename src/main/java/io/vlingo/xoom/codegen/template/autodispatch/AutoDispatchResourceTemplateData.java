// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.autodispatch;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.storage.QueriesParameter;

import java.util.List;

import static io.vlingo.xoom.codegen.parameter.Label.AUTO_DISPATCH;
import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static java.util.stream.Collectors.toList;

public class AutoDispatchResourceTemplateData extends TemplateData {

    private final TemplateParameters parameters;

    public static List<TemplateData> from(final CodeGenerationContext context) {
        return context.parametersOf(AUTO_DISPATCH).map(AutoDispatchResourceTemplateData::new).collect(toList());
    }

    public AutoDispatchResourceTemplateData(final CodeGenerationParameter autoDispatchParameter) {
        this.parameters =
                TemplateParameters.with(PACKAGE_NAME, resolveAutoDispatchPackage(autoDispatchParameter.value))
                        .and(REST_RESOURCE_NAME, resolveAutoDispatchClassName(autoDispatchParameter.value))
                        .and(QUERIES, QueriesParameter.from(autoDispatchParameter))
                        .and(URI_ROOT, "/TODO/").and(ROUTES, RouteParameter.from(autoDispatchParameter))
                        .and(MODEL_PROTOCOL, autoDispatchParameter.relatedParameterValueOf(Label.MODEL_PROTOCOL))
                        .and(MODEL_ACTOR, autoDispatchParameter.relatedParameterValueOf(Label.MODEL_ACTOR));

        this.dependOn(RouteMethodTemplateData.from(autoDispatchParameter, parameters));
    }

    @Override
    public void handleDependencyOutcome(final TemplateStandard standard, String outcome) {

    }

    private String resolveAutoDispatchClassName(final String qualifiedName) {
        return qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
    }

    private String resolveAutoDispatchPackage(final String qualifiedName) {
        return qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public TemplateStandard standard() {
        return TemplateStandard.AUTO_DISPATCH_RESOURCE;
    }

}
