// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.autodispatch;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import static io.vlingo.xoom.codegen.template.TemplateStandard.ROUTE_METHOD;

public class RouteMethodTemplateData extends TemplateData {

    private final TemplateParameters parameters;

    public RouteMethodTemplateData(final CodeGenerationParameter autoDispatchParameter,
                                   final TemplateParameters parameters) {
        this.parameters = parameters;
    }

    public static TemplateData from(CodeGenerationParameter autoDispatchParameter, TemplateParameters parameters) {
        return null;
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public TemplateStandard standard() {
        return ROUTE_METHOD;
    }
}
