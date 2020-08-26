// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.bootstrap;

import io.vlingo.common.identity.IdentityGeneratorType;
import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.template.TemplateParameter;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.REST_RESOURCE;
import static io.vlingo.xoom.codegen.template.TemplateStandard.XOOM_INITIALIZER;

public class XoomInitializerTemplateData extends BootstrapTemplateData {

    @Override
    protected void enrichParameters(final CodeGenerationContext context) {
        final List<Content> contents = context.contents();
        final String appName = context.parameterOf(APPLICATION_NAME);
        final String xoomInitializerClass = context.parameterOf(XOOM_INITIALIZER_NAME);
        final Boolean blockingMessaging = context.parameterOf(BLOCKING_MESSAGING, Boolean::valueOf);
        final AddressFactoryType addressFactoryType = context.parameterOf(ADDRESS_FACTORY, AddressFactoryType::valueOf);
        final IdentityGeneratorType identityGeneratorType = context.parameterOf(IDENTITY_GENERATOR, IdentityGeneratorType::valueOf);
        final String stageInstantiationVariables = addressFactoryType.resolveParameters(appName, identityGeneratorType);
        final Boolean customInitialization = !xoomInitializerClass.equals(XOOM_INITIALIZER.resolveClassname());

        loadImports(addressFactoryType, contents);

        parameters().and(TemplateParameter.BLOCKING_MESSAGING, blockingMessaging)
                .and(TemplateParameter.XOOM_INITIALIZER_CLASS, xoomInitializerClass)
                .and(TemplateParameter.CUSTOM_INITIALIZATION, customInitialization)
                .and(TemplateParameter.REST_RESOURCES, RestResourcesParameter.from(contents))
                .and(TemplateParameter.STAGE_INSTANTIATION_VARIABLES, stageInstantiationVariables);
    }

    private void loadImports(final AddressFactoryType addressFactoryType,
                             final List<Content> contents) {

        final List<String> addressFactoryImports =
                addressFactoryType.isBasic() ? Collections.emptyList() :
                        Arrays.asList(addressFactoryType.qualifiedName,
                                IdentityGeneratorType.class.getCanonicalName());

        parameters().addImports(addressFactoryImports)
                .addImports(ContentQuery.findFullyQualifiedClassNames(REST_RESOURCE, contents));
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