// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.model;

import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.DOMAIN_EVENT;

public class DomainEventTemplateData extends TemplateData {

    private final String name;
    private final TemplateParameters parameters;

    public static List<TemplateData> from(final String aggregateProtocolName,
                                          final String packageName,
                                          final List<String> eventsNames) {
        final List<TemplateData> eventsTemplateData = new ArrayList<>();

        eventsTemplateData.add(new DomainEventTemplateData(packageName,
                aggregateProtocolName, true));

        eventsTemplateData.addAll(eventsNames.stream().map(eventName ->
                new DomainEventTemplateData(packageName, eventName, false))
                .collect(Collectors.toList()));

        return eventsTemplateData;
    }

    private DomainEventTemplateData(final String packageName,
                                    final String domainEventName,
                                    final Boolean placeholder) {
        this.name = domainEventName;
        this.parameters =
                TemplateParameters.with(PACKAGE_NAME, packageName).and(PLACEHOLDER_EVENT, placeholder)
                        .andResolve(DOMAIN_EVENT_NAME, param -> DOMAIN_EVENT.resolveClassname(domainEventName, param));
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public String filename() {
        return standard().resolveFilename(name, parameters);
    }

    @Override
    public TemplateStandard standard() {
        return DOMAIN_EVENT;
    }

}
