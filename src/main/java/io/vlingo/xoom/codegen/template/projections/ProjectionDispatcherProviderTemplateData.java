// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.projections;

import io.vlingo.xoom.codegen.Content;
import io.vlingo.xoom.codegen.ContentQuery;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import java.util.List;

import static io.vlingo.xoom.codegen.template.TemplateParameter.PACKAGE_NAME;
import static io.vlingo.xoom.codegen.template.TemplateParameter.PROJECTION_TO_DESCRIPTION;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE_PROTOCOL;
import static io.vlingo.xoom.codegen.template.TemplateStandard.PROJECTION_DISPATCHER_PROVIDER;

public class ProjectionDispatcherProviderTemplateData extends TemplateData {

    private static final String PACKAGE_PATTERN = "%s.%s.%s";
    private static final String PARENT_PACKAGE_NAME = "infrastructure";
    private static final String PERSISTENCE_PACKAGE_NAME = "persistence";

    private final TemplateParameters templateParameters;

    public static ProjectionDispatcherProviderTemplateData from(final String basePackage,
                                                                final ProjectionType projectionType,
                                                                final List<Content> contents) {
        final List<String> aggregateProtocols = ContentQuery.findClassNames(AGGREGATE_PROTOCOL, contents);
        return new ProjectionDispatcherProviderTemplateData(basePackage, projectionType, aggregateProtocols);
    }

    private ProjectionDispatcherProviderTemplateData(final String basePackage,
                                                     final ProjectionType projectionType,
                                                     final List<String> aggregateProtocols) {
        final String packageName = resolvePackage(basePackage);
        this.templateParameters = loadParameters(packageName, projectionType, aggregateProtocols);
    }

    private TemplateParameters loadParameters(final String packageName,
                                              final ProjectionType projectionType,
                                              final List<String> aggregateProtocols) {
        final List<ProjectToDescriptionParameter> projectToDescriptionParameters =
                ProjectToDescriptionParameter.from(projectionType, aggregateProtocols);

        return TemplateParameters.with(PACKAGE_NAME, packageName)
                .and(PROJECTION_TO_DESCRIPTION, projectToDescriptionParameters);
    }

    private String resolvePackage(final String basePackage) {
        return String.format(PACKAGE_PATTERN, basePackage, PARENT_PACKAGE_NAME, PERSISTENCE_PACKAGE_NAME).toLowerCase();
    }

    @Override
    public TemplateParameters parameters() {
        return templateParameters;
    }

    @Override
    public TemplateStandard standard() {
        return PROJECTION_DISPATCHER_PROVIDER;
    }

}
