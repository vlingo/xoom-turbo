// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.projections;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.file.ImportParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import java.util.List;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.*;
import static io.vlingo.xoom.codegen.template.storage.ModelClassification.QUERY;
import static io.vlingo.xoom.codegen.template.storage.StorageType.STATE_STORE;

public class ProjectionTemplateData extends TemplateData {

    private static final String PACKAGE_PATTERN = "%s.%s.%s";
    private static final String PARENT_PACKAGE_NAME = "infrastructure";
    private static final String PERSISTENCE_PACKAGE_NAME = "persistence";

    private final String protocolName;
    private final TemplateParameters parameters;

    public static ProjectionTemplateData from(final String basePackage,
                                              final String protocolName,
                                              final List<Content> contents,
                                              final ProjectionType projectionType,
                                              final TemplateData entityDataTemplateData) {
        return new ProjectionTemplateData(basePackage, protocolName,
                contents, projectionType, entityDataTemplateData);
    }

    private ProjectionTemplateData (final String basePackage,
                                    final String protocolName,
                                    final List<Content> contents,
                                    final ProjectionType projectionType,
                                    final TemplateData entityDataTemplateData) {
        this.parameters =
                loadParameters(resolvePackage(basePackage), protocolName,
                        contents, projectionType, entityDataTemplateData);

        this.protocolName = protocolName;
    }

    private TemplateParameters loadParameters(final String packageName,
                                              final String protocolName,
                                              final List<Content> contents,
                                              final ProjectionType projectionType,
                                              final TemplateData entityDataTemplateData) {
        final String stateName = STATE.resolveClassname(protocolName);
        final String projectionName = PROJECTION.resolveClassname(protocolName);
        final String entityDataName = ENTITY_DATA.resolveClassname(protocolName);

        final List<ImportParameter> imports =
                resolveImports(stateName, contents, entityDataTemplateData.parameters());

        return TemplateParameters.with(PACKAGE_NAME, packageName).and(IMPORTS, imports)
                .and(PROJECTION_NAME, projectionName).and(STATE_NAME, stateName)
                .and(MODEL_CLASSIFICATION, QUERY).and(STORAGE_TYPE, STATE_STORE)
                .and(ENTITY_DATA_NAME, entityDataName).and(PROJECTION_TYPE, projectionType)
                .andResolve(STORAGE_PROVIDER_NAME, param -> STORE_PROVIDER.resolveClassname(param));
    }

    private List<ImportParameter> resolveImports(final String stateName,
                                                 final List<Content> contents,
                                                 final TemplateParameters entityDataTemplateParameters) {
        final String stateQualifiedName =
                ContentQuery.findFullyQualifiedClassName(STATE, stateName, contents);

        final String entityDataQualifiedName =
                entityDataTemplateParameters.find(ENTITY_DATA_QUALIFIED_CLASS_NAME);

        return ImportParameter.of(stateQualifiedName, entityDataQualifiedName);
    }

    private String resolvePackage(final String basePackage) {
        return String.format(PACKAGE_PATTERN, basePackage, PARENT_PACKAGE_NAME,
                PERSISTENCE_PACKAGE_NAME).toLowerCase();
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
        return PROJECTION;
    }

}
