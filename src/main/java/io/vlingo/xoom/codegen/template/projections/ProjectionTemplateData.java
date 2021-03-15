// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.projections;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.parameter.ImportParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.storage.StorageType;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.*;
import static io.vlingo.xoom.codegen.template.storage.Model.QUERY;
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
                                              final StorageType storageType,
                                              final List<TemplateData> templatesData) {
        return new ProjectionTemplateData(basePackage, protocolName,
                contents, projectionType, storageType, templatesData);
    }

    private ProjectionTemplateData(final String basePackage,
                                   final String protocolName,
                                   final List<Content> contents,
                                   final ProjectionType projectionType,
                                   final StorageType storageType,
                                   final List<TemplateData> templatesData) {
        this.parameters =
                loadParameters(resolvePackage(basePackage), protocolName,
                        contents, projectionType, storageType, templatesData);

        this.protocolName = protocolName;
    }

    private TemplateParameters loadParameters(final String packageName,
                                              final String protocolName,
                                              final List<Content> contents,
                                              final ProjectionType projectionType,
                                              final StorageType storageType,
                                              final List<TemplateData> templatesData) {
        final String stateName = AGGREGATE_STATE.resolveClassname(protocolName);
        final String projectionName = PROJECTION.resolveClassname(protocolName);
        final String dataObjectName = DATA_OBJECT.resolveClassname(protocolName);
        final String modelPackage = ContentQuery.findPackage(AGGREGATE_STATE, stateName, contents);

        final Set<ImportParameter> imports =
                resolveImports(stateName, dataObjectName, contents,
                        projectionType, templatesData);

        return TemplateParameters.with(PACKAGE_NAME, packageName).and(IMPORTS, imports)
                .and(PROJECTION_NAME, projectionName).and(STATE_NAME, stateName)
                .and(PROJECTION_TYPE, projectionType).and(MODEL, QUERY)
                .and(STORAGE_TYPE, STATE_STORE).and(STATEFUL, storageType.isStateful())
                .and(STATE_DATA_OBJECT_NAME, dataObjectName).and(PROJECTION_TYPE, projectionType)
                .and(PROJECTION_SOURCE_NAMES, ContentQuery.findClassNames(DOMAIN_EVENT, modelPackage, contents))
                .andResolve(PROJECTION_SOURCE_TYPES_NAME, param -> PROJECTION_SOURCE_TYPES.resolveClassname(param))
                .andResolve(STORE_PROVIDER_NAME, param -> STORE_PROVIDER.resolveClassname(param));
    }

    private Set<ImportParameter> resolveImports(final String stateName,
                                                final String dataObjectName,
                                                final List<Content> contents,
                                                final ProjectionType projectionType,
                                                final List<TemplateData> templatesData) {
        final String stateQualifiedName =
                ContentQuery.findFullyQualifiedClassName(AGGREGATE_STATE, stateName, contents);

        final String dataObjectQualifiedName =
                ContentQuery.findFullyQualifiedClassName(DATA_OBJECT, dataObjectName, contents);

        return templatesData.stream().filter(data -> data.hasStandard(PROJECTION_SOURCE_TYPES))
                .map(data -> data.parameters().<String>find(PROJECTION_SOURCE_TYPES_QUALIFIED_NAME))
                .map(qualifiedName -> ImportParameter.of(stateQualifiedName, dataObjectQualifiedName, qualifiedName))
                .flatMap(imports -> imports.stream()).collect(Collectors.toSet());
    }

    private String resolvePackage(final String basePackage) {
        return String.format(PACKAGE_PATTERN, basePackage, PARENT_PACKAGE_NAME,
                PERSISTENCE_PACKAGE_NAME).toLowerCase();
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public TemplateStandard standard() {
        return PROJECTION;
    }

    @Override
    public String filename() {
        return standard().resolveFilename(protocolName, parameters);
    }

}
