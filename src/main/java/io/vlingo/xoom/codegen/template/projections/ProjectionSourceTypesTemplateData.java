// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.projections;

import io.vlingo.xoom.codegen.content.ClassFormatter;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import java.util.List;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.DOMAIN_EVENT;
import static io.vlingo.xoom.codegen.template.TemplateStandard.PROJECTION_SOURCE_TYPES;

public class ProjectionSourceTypesTemplateData extends TemplateData {

    private final static String PACKAGE_PATTERN = "%s.%s";
    private final static String INFRASTRUCTURE_PACKAGE = "infrastructure";

    private final TemplateParameters parameters;

    public static ProjectionSourceTypesTemplateData from(final String basePackage,
                                                         final ProjectionType projectionType,
                                                         final List<Content> contents) {
        return new ProjectionSourceTypesTemplateData(basePackage, projectionType, contents);
    }

    private ProjectionSourceTypesTemplateData(final String basePackage,
                                              final ProjectionType projectionType,
                                              final List<Content> contents) {
        final String packageName = resolvePackage(basePackage);

        this.parameters =
                TemplateParameters.with(PACKAGE_NAME, packageName).and(PROJECTION_TYPE, projectionType)
                        .and(PROJECTION_SOURCE_NAMES, ContentQuery.findClassNames(DOMAIN_EVENT, contents))
                        .andResolve(PROJECTION_SOURCE_TYPES_NAME, this::resolveClassName)
                        .andResolve(PROJECTION_SOURCE_TYPES_QUALIFIED_NAME, this::resolveQualifiedName);
    }

    private String resolvePackage(final String basePackage) {
        return String.format(PACKAGE_PATTERN, basePackage, INFRASTRUCTURE_PACKAGE).toLowerCase();
    }

    private String resolveClassName(final TemplateParameters parameters) {
        return PROJECTION_SOURCE_TYPES.resolveClassname(parameters);
    }

    private String resolveQualifiedName(final TemplateParameters parameters) {
        final String packageName = parameters.find(PACKAGE_NAME);
        final String className = parameters.find(PROJECTION_SOURCE_TYPES_NAME);
        return ClassFormatter.qualifiedNameOf(packageName, className);
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public TemplateStandard standard() {
        return PROJECTION_SOURCE_TYPES;
    }

    @Override
    public String filename() {
        return PROJECTION_SOURCE_TYPES.resolveFilename(parameters);
    }

}
