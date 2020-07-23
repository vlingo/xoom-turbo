// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.projections;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import java.util.List;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.ENTITY_DATA;
import static io.vlingo.xoom.codegen.template.TemplateStandard.STATE;

public class EntityDataTemplateData extends TemplateData {

    private final static String PACKAGE_PATTERN = "%s.%s";
    private final static String PARENT_PACKAGE_NAME = "infrastructure";

    private final String protocolName;
    private final TemplateParameters parameters;

    public static EntityDataTemplateData from(final String basePackage,
                                              final String protocolName,
                                              final List<Content> contents) {
        return new EntityDataTemplateData(basePackage, protocolName, contents);
    }

    private EntityDataTemplateData(final String basePackage,
                                   final String protocolName,
                                   final List<Content> contents) {
        this.protocolName = protocolName;
        final String packageName = resolvePackage(basePackage);
        this.parameters = loadParameters(packageName, protocolName, contents);
    }

    private TemplateParameters loadParameters(final String packageName,
                                              final String protocolName,
                                              final List<Content> contents) {
        final String stateName =
                STATE.resolveClassname(protocolName);

        final String stateQualifiedClassName =
                ContentQuery.findFullyQualifiedClassName(STATE, stateName, contents);

        final String dataName =
                ENTITY_DATA.resolveClassname(protocolName);

        final String entityDataQualifiedClassName = packageName.concat(".").concat(dataName);

        return TemplateParameters.with(PACKAGE_NAME, packageName)
                            .and(ENTITY_DATA_NAME, dataName)
                            .and(STATE_NAME, stateName)
                            .and(ENTITY_DATA_QUALIFIED_CLASS_NAME, entityDataQualifiedClassName)
                            .and(STATE_QUALIFIED_CLASS_NAME, stateQualifiedClassName);
    }

    private String resolvePackage(final String basePackage) {
        return String.format(PACKAGE_PATTERN, basePackage,
                PARENT_PACKAGE_NAME).toLowerCase();
    }

    @Override
    public String filename() {
        return standard().resolveFilename(protocolName, parameters);
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public TemplateStandard standard() {
        return ENTITY_DATA;
    }

}
