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

import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE_PROTOCOL;

public class ProjectionTemplateDataFactory {

    public static List<TemplateData> build(final String basePackage,
                                           final ProjectionType projectionType,
                                           final List<Content> contents) {
        final List<String> aggregateProtocols =
                ContentQuery.findClassNames(AGGREGATE_PROTOCOL, contents);

        final List<TemplateData> templatesData =
                newArrayList(ProjectionDispatcherProviderTemplateData.from(basePackage,
                        projectionType, contents));

        aggregateProtocols.stream().forEach(protocolName -> {
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
