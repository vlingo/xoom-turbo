// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.bootstrap;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.template.TemplateParameter;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import java.util.List;

import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.*;

public class XoomInitializerTemplateData extends BootstrapTemplateData {

    @Override
    protected void enrichParameters(final CodeGenerationContext context) {
        final List<Content> contents = context.contents();
        final String appName = context.parameterOf(APPLICATION_NAME);
        final String xoomInitializerClass = context.parameterOf(XOOM_INITIALIZER_NAME);
        final Boolean blockingMessaging = context.parameterOf(BLOCKING_MESSAGING, Boolean::valueOf);
        final Boolean customInitialization = !xoomInitializerClass.equals(XOOM_INITIALIZER.resolveClassname());

        loadImports(contents);

        parameters().and(TemplateParameter.BLOCKING_MESSAGING, blockingMessaging)
                .and(TemplateParameter.XOOM_INITIALIZER_CLASS, xoomInitializerClass)
                .and(TemplateParameter.CUSTOM_INITIALIZATION, customInitialization)
                .and(TemplateParameter.REST_RESOURCES, RestResource.from(contents))
                .and(TemplateParameter.APPLICATION_NAME, appName);
    }

    private void loadImports(final List<Content> contents) {
        parameters().addImports(ContentQuery.findFullyQualifiedClassNames(contents, REST_RESOURCE, AUTO_DISPATCH_RESOURCE_HANDLER));
    }

    @Override
    protected String resolvePackage(final String basePackage) {
        return basePackage;
    }

    @Override
    protected boolean support(final CodeGenerationContext context) {
        return context.isInternalGeneration();
    }

    @Override
    public TemplateStandard standard() {
        return TemplateStandard.XOOM_INITIALIZER;
    }

}