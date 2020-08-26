// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.bootstrap;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.content.ContentQuery;

import static io.vlingo.xoom.codegen.parameter.Label.ANNOTATIONS;
import static io.vlingo.xoom.codegen.template.TemplateParameter.REST_RESOURCES;
import static io.vlingo.xoom.codegen.template.TemplateStandard.REST_RESOURCE;

public class DefaultBootstrapTemplateData extends BootstrapTemplateData {

    @Override
    protected void enrichParameters(final CodeGenerationContext context) {
        parameters().and(REST_RESOURCES, RestResourcesParameter.from(context.contents()))
                .addImports(ContentQuery.findFullyQualifiedClassNames(REST_RESOURCE, context.contents()));
    }

    @Override
    protected boolean support(final CodeGenerationContext context) {
        return !context.isInternalGeneration() &&
                !context.parameterOf(ANNOTATIONS, Boolean::valueOf);
    }

}
