// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.bootstrap;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.content.ContentQuery;

import java.util.Set;

import static io.vlingo.xoom.codegen.parameter.Label.ANNOTATIONS;
import static io.vlingo.xoom.codegen.template.TemplateParameter.REST_RESOURCES;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AUTO_DISPATCH_RESOURCE_HANDLER;
import static io.vlingo.xoom.codegen.template.TemplateStandard.REST_RESOURCE;

public class DefaultBootstrapTemplateData extends BootstrapTemplateData {

    @Override
    protected void enrichParameters(final CodeGenerationContext context) {
        final Set<String> qualifiedNames =
                ContentQuery.findFullyQualifiedClassNames(context.contents(),
                        REST_RESOURCE, AUTO_DISPATCH_RESOURCE_HANDLER);

        parameters().and(REST_RESOURCES, RestResourcesParameter.from(context.contents()))
                .addImports(qualifiedNames);
    }

    @Override
    protected boolean support(final CodeGenerationContext context) {
        return !context.isInternalGeneration() &&
                !context.parameterOf(ANNOTATIONS, Boolean::valueOf);
    }

}
