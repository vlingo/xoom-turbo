// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.codegen.template.storage;

import io.vlingo.xoom.turbo.codegen.template.TemplateData;
import io.vlingo.xoom.turbo.codegen.template.TemplateParameters;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.ADAPTER_NAME;
import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.SOURCE_NAME;
import static io.vlingo.xoom.turbo.codegen.template.DesignerTemplateStandard.ADAPTER;

public class Adapter {

  private final String sourceClass;
  private final String adapterClass;
  private final boolean last;

  private Adapter(final int index,
                  final int numberOfAdapters,
                  final String sourceClass,
                  final String adapterClass) {
    this.sourceClass = sourceClass;
    this.adapterClass = adapterClass;
    this.last = index == numberOfAdapters - 1;
  }

  private Adapter(final int index, final int numberOfAdapters, final TemplateParameters parameters) {
    this(index, numberOfAdapters, parameters.find(SOURCE_NAME), parameters.find(ADAPTER_NAME));
  }

  public static List<Adapter> from(final List<TemplateData> templatesData) {
    final Predicate<TemplateData> filter =
            templateData -> templateData.hasStandard(ADAPTER);

    final List<TemplateData> adapterTemplates =
            templatesData.stream().filter(filter).collect(Collectors.toList());

    return IntStream.range(0, adapterTemplates.size()).mapToObj(index -> {
      final TemplateData templateData = adapterTemplates.get(index);
      return new Adapter(index, adapterTemplates.size(),
              templateData.parameters());
    }).collect(Collectors.toList());
  }

  public String getSourceClass() {
    return sourceClass;
  }

  public String getAdapterClass() {
    return adapterClass;
  }

  public boolean isLast() {
    return last;
  }

}
