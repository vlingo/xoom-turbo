// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.codegen.storage;

import io.vlingo.xoom.codegen.content.CodeElementFormatter;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.turbo.annotation.codegen.AnnotationBasedTemplateStandard;
import io.vlingo.xoom.turbo.annotation.codegen.TemplateParameter;
import io.vlingo.xoom.turbo.annotation.codegen.projections.ProjectionType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.template.ParameterKey.Defaults.PACKAGE_NAME;
import static java.util.stream.Collectors.toSet;

public class StorageProviderTemplateData extends TemplateData {

  private final TemplateParameters templateParameters;

  public static List<TemplateData> from(final String persistencePackage,
                                        final StorageType storageType,
                                        final ProjectionType projectionType,
                                        final List<TemplateData> stateAdaptersTemplateData,
                                        final List<Content> contents,
                                        final Stream<Model> models) {
    return models.sorted().map(model -> new StorageProviderTemplateData(persistencePackage,
            storageType, projectionType, stateAdaptersTemplateData, contents, model))
            .collect(Collectors.toList());
  }

  protected StorageProviderTemplateData(final String persistencePackage,
                                        final StorageType storageType,
                                        final ProjectionType projectionType,
                                        final List<TemplateData> stateAdaptersTemplateData,
                                        final List<Content> contents,
                                        final Model model) {
    this.templateParameters =
            loadParameters(persistencePackage, storageType, projectionType,
                    stateAdaptersTemplateData, contents, model);
  }

  private TemplateParameters loadParameters(final String packageName,
                                            final StorageType storageType,
                                            final ProjectionType projectionType,
                                            final List<TemplateData> templatesData,
                                            final List<Content> contents,
                                            final Model model) {
    final List<Queries> queries =
            Queries.from(model, contents, templatesData);

    final Stream<String> persistentTypes =
            storageType.findPersistentQualifiedTypes(model, contents).stream();

    final Set<String> imports = resolveImports(model, storageType, contents, queries);

    return TemplateParameters.with(TemplateParameter.STORAGE_TYPE, storageType).and(TemplateParameter.PROJECTION_TYPE, projectionType)
            .and(TemplateParameter.MODEL, model).and(PACKAGE_NAME, packageName).and(TemplateParameter.ADAPTERS, Adapter.from(templatesData))
            .and(TemplateParameter.REQUIRE_ADAPTERS, storageType.requireAdapters(model)).and(TemplateParameter.QUERIES, queries)
            .and(TemplateParameter.USE_PROJECTIONS, projectionType.isProjectionEnabled())
            .and(TemplateParameter.AGGREGATES, ContentQuery.findClassNames(AnnotationBasedTemplateStandard.AGGREGATE, contents))
            .and(TemplateParameter.PERSISTENT_TYPES, persistentTypes.map(CodeElementFormatter::simpleNameOf).collect(toSet()))
            .andResolve(TemplateParameter.STORE_PROVIDER_NAME, params -> AnnotationBasedTemplateStandard.STORE_PROVIDER.resolveClassname(params))
            .addImports(imports);
  }

  private Set<String> resolveImports(final Model model,
                                     final StorageType storageType,
                                     final List<Content> contents,
                                     final List<Queries> queries) {
    final Set<String> sourceClassQualifiedNames =
            storageType.resolveAdaptersQualifiedName(model, contents);

    final Set<String> persistentTypes =
            storageType.findPersistentQualifiedTypes(model, contents);

    final Set<String> queriesQualifiedNames = queries.stream()
            .flatMap(param -> param.getQualifiedNames().stream())
            .collect(toSet());

    final Set<String> aggregateActorQualifiedNames = storageType.isSourced() ?
            ContentQuery.findFullyQualifiedClassNames(AnnotationBasedTemplateStandard.AGGREGATE, contents) : new HashSet<>();

    return Stream.of(sourceClassQualifiedNames, queriesQualifiedNames,
            aggregateActorQualifiedNames, persistentTypes).flatMap(s -> s.stream())
            .collect(Collectors.toSet());
  }

  @Override
  public TemplateParameters parameters() {
    return templateParameters;
  }

  @Override
  public TemplateStandard standard() {
    return AnnotationBasedTemplateStandard.STORE_PROVIDER;
  }

}
