// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.projections;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.template.TemplateData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static io.vlingo.xoom.codegen.CodeGenerationParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE_PROTOCOL;

public class ProjectionTemplateDataFactory {

    private static ProjectionTemplateDataFactory instance;

    public static List<TemplateData> build(final CodeGenerationContext context) {
        if(instance == null) {
            instance = new ProjectionTemplateDataFactory();
        }
        return context.isInternalGeneration() ?
                instance.handleInternalGeneration(context) :
                instance.handleExternalGeneration(context);
    }

    private List<TemplateData> handleInternalGeneration(final CodeGenerationContext context) {
        return Arrays.asList(ProjectionDispatcherProviderTemplateData.from(
                context.parameterOf(PROJECTABLES), context.contents()));
    }

    private List<TemplateData> handleExternalGeneration(final CodeGenerationContext context) {
        final List<Content> contents = context.contents();
        final String basePackage = context.parameterOf(PACKAGE);
        final ProjectionType projectionType = context.parameterOf(PROJECTION_TYPE, ProjectionType::valueOf);

        final List<String> aggregateProtocols =
                ContentQuery.findClassNames(AGGREGATE_PROTOCOL, contents);

        final List<TemplateData> templatesData = new ArrayList<>();

        if(!context.parameterOf(ANNOTATIONS, Boolean::valueOf)) {
            templatesData.add(ProjectionDispatcherProviderTemplateData.from(basePackage,
                    projectionType, contents));
        }

        aggregateProtocols.forEach(protocolName -> {
            final TemplateData entityData =
                    EntityDataTemplateData.from(basePackage, protocolName, contents);

            final TemplateData projectionData =
                    ProjectionTemplateData.from(basePackage, protocolName,
                            contents, projectionType, entityData);

            Collections.addAll(templatesData, entityData, projectionData);
        });

        return templatesData;
    }

}
