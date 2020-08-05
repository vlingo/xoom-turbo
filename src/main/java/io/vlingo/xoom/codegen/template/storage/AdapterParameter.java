// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.storage;

import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.vlingo.xoom.codegen.template.TemplateParameter.ADAPTER_NAME;
import static io.vlingo.xoom.codegen.template.TemplateParameter.SOURCE_NAME;

public class AdapterParameter {

    private final String sourceClass;
    private final String adapterClass;
    private final boolean last;

    private AdapterParameter(final int index,
                             final int numberOfAdapters,
                             final String sourceClass,
                             final String adapterClass) {
        this.sourceClass = sourceClass;
        this.adapterClass = adapterClass;
        this.last = index == numberOfAdapters - 1;
    }

    private AdapterParameter(final int index, final int numberOfAdapters, final TemplateParameters parameters) {
        this(index, numberOfAdapters, parameters.find(SOURCE_NAME), parameters.find(ADAPTER_NAME));
    }

    public static List<AdapterParameter> from(final List<TemplateData> adaptersTemplateData) {
        return IntStream.range(0, adaptersTemplateData.size()).mapToObj(index -> {
            final TemplateData templateData = adaptersTemplateData.get(index);
            return new AdapterParameter(index, adaptersTemplateData.size(),
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
