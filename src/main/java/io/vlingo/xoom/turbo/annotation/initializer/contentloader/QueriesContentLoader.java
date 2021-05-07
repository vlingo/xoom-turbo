// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.initializer.contentloader;

import io.vlingo.xoom.turbo.annotation.persistence.EnableQueries;
import io.vlingo.xoom.turbo.annotation.persistence.QueriesEntry;
import io.vlingo.xoom.turbo.codegen.CodeGenerationContext;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.turbo.annotation.codegen.template.AnnotationBasedTemplateStandard.QUERIES_ACTOR;


public class QueriesContentLoader extends ContentLoader<Map<TypeElement, TypeElement>> {

  protected QueriesContentLoader(final Element annotatedClass, final ProcessingEnvironment environment) {
    super(annotatedClass, environment);
  }

  @Override
  public void load(final CodeGenerationContext context) {
    this.retrieveContentSource().entrySet()
            .forEach(entry -> context.addContent(QUERIES_ACTOR, entry.getKey(), entry.getValue()));
  }

  @Override
  protected Map<TypeElement, TypeElement> retrieveContentSource() {
    final EnableQueries queries = annotatedClass.getAnnotation(EnableQueries.class);

    if (queries == null) {
      return Collections.emptyMap();
    }

    return Stream.of(queries.value()).map(queriesEntry -> {
      final TypeElement protocolType =
              typeRetriever.from(queriesEntry, QueriesEntry::protocol);

      final TypeElement actorType =
              typeRetriever.from(queriesEntry, QueriesEntry::actor);

      return new SimpleEntry<>(protocolType, actorType);
    }).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
  }

}