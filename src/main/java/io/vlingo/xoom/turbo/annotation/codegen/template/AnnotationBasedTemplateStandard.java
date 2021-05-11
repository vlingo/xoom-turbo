// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.annotation.codegen.template;

import io.vlingo.xoom.http.Method;
import io.vlingo.xoom.turbo.annotation.codegen.template.storage.Model;
import io.vlingo.xoom.turbo.annotation.codegen.template.storage.StorageType;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import java.util.function.BiFunction;
import java.util.function.Function;

import static io.vlingo.xoom.turbo.annotation.codegen.template.Configuration.*;
import static io.vlingo.xoom.turbo.annotation.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.turbo.annotation.codegen.template.storage.StorageType.STATE_STORE;

public enum AnnotationBasedTemplateStandard implements TemplateStandard {

  AUTO_DISPATCH_RESOURCE_HANDLER(parameters -> Template.REST_RESOURCE.filename,
          (name, parameters) -> name + "Handler"),

  ROUTE_METHOD(parameters -> {
    final String httpMethod =
            parameters.find(TemplateParameter.ROUTE_METHOD);

    if (Method.from(httpMethod).isGET()) {
      return Template.REST_RESOURCE_RETRIEVE_METHOD.filename;
    }

    if (parameters.find(REQUIRE_ENTITY_LOADING, false)) {
      return Template.REST_RESOURCE_UPDATE_METHOD.filename;
    }

    return Template.REST_RESOURCE_CREATION_METHOD.filename;
  }, (name, parameters) -> name),

  ADAPTER(parameters -> ADAPTER_TEMPLATES.get(parameters.find(STORAGE_TYPE)),
          (name, parameters) -> name + "Adapter"),

  PROJECTION_DISPATCHER_PROVIDER(parameters -> Template.PROJECTION_DISPATCHER_PROVIDER.filename,
          (name, parameters) -> "ProjectionDispatcherProvider"),

  XOOM_INITIALIZER(templateParameters -> Template.XOOM_INITIALIZER.filename,
          (name, parameters) -> "XoomInitializer"),

  STORE_PROVIDER(parameters -> {
            final StorageType storageType = parameters.find(STORAGE_TYPE);
            if (parameters.<Model>find(MODEL).isQueryModel()) {
              return QUERY_MODEL_STORE_TEMPLATES.get(storageType);
            }
            return COMMAND_MODEL_STORE_TEMPLATES.get(storageType);
        },
          (name, parameters) -> {
            final StorageType storageType = parameters.find(STORAGE_TYPE);
            final Model model = parameters.find(MODEL);
            if (model.isQueryModel()) {
              return STATE_STORE.resolveProviderNameFrom(model);
            }
            return storageType.resolveProviderNameFrom(model);
          }),

  AGGREGATE(),
  AGGREGATE_STATE(),
  DATA_OBJECT(),
  DOMAIN_EVENT(),
  EXCHANGE_BOOTSTRAP(),
  PROJECTION(),
  REST_RESOURCE(),
  QUERIES(),
  QUERIES_ACTOR();

  private final Function<TemplateParameters, String> templateFileRetriever;
  private final BiFunction<String, TemplateParameters, String> nameResolver;

  AnnotationBasedTemplateStandard() {
    this(params -> null);
  }


  AnnotationBasedTemplateStandard(final Function<TemplateParameters, String> templateFileRetriever) {
    this(templateFileRetriever, (name, parameters) -> name);
  }

  AnnotationBasedTemplateStandard(final Function<TemplateParameters, String> templateFileRetriever,
                           final BiFunction<String, TemplateParameters, String> nameResolver) {
    this.templateFileRetriever = templateFileRetriever;
    this.nameResolver = nameResolver;
  }

  public String retrieveTemplateFilename(final TemplateParameters parameters) {
    return templateFileRetriever.apply(parameters);
  }

  public String resolveClassname() {
    return resolveClassname("");
  }

  public String resolveClassname(final String name) {
    return resolveClassname(name, null);
  }

  public String resolveClassname(final TemplateParameters parameters) {
    return resolveClassname(null, parameters);
  }

  public String resolveClassname(final String name, final TemplateParameters parameters) {
    return this.nameResolver.apply(name, parameters);
  }

  public String resolveFilename(final TemplateParameters parameters) {
    return resolveFilename(null, parameters);
  }

  public String resolveFilename(final String name, final TemplateParameters parameters) {
    return this.nameResolver.apply(name, parameters);
  }

}
