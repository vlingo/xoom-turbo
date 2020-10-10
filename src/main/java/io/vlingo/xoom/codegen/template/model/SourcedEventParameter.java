// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.model;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;

import java.util.List;

import static io.vlingo.xoom.codegen.parameter.Label.DOMAIN_EVENT;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE;
import static java.util.stream.Collectors.toList;

public class SourcedEventParameter {

    private final String entityName;
    private final String domainEventName;

    public static List<SourcedEventParameter> from(final CodeGenerationParameter aggregate) {
        return aggregate.retrieveAll(DOMAIN_EVENT)
                .map(event -> new SourcedEventParameter(aggregate, event))
                .collect(toList());
    }

    private SourcedEventParameter(final CodeGenerationParameter aggregate,
                                  final CodeGenerationParameter event) {
        this.domainEventName = event.value;
        this.entityName = AGGREGATE.resolveClassname(aggregate.value);
    }

    public String getEntityName() {
        return entityName;
    }

    public String getDomainEventName() {
        return domainEventName;
    }

}