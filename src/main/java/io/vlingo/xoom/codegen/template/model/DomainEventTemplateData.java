// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.model;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.STATE_FIELD;
import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.DOMAIN_EVENT;
import static io.vlingo.xoom.codegen.template.model.AggregateFieldsFormat.*;

public class DomainEventTemplateData extends TemplateData {

    private final String name;
    private final TemplateParameters parameters;

    public static List<TemplateData> from(final String packageName,
                                          final CodeGenerationParameter aggregate) {
        return aggregate.retrieveAll(Label.DOMAIN_EVENT).map(event ->
                new DomainEventTemplateData(packageName, event.value, aggregate, event.retrieveAll(STATE_FIELD)))
                .collect(Collectors.toList());
    }

    private DomainEventTemplateData(final String packageName,
                                    final String eventName,
                                    final CodeGenerationParameter aggregate,
                                    final Stream<CodeGenerationParameter> eventFields) {
        this.name = eventName;
        this.parameters =
                TemplateParameters.with(PACKAGE_NAME, packageName).and(DOMAIN_EVENT_NAME, name)
                        .and(MEMBERS, MEMBER_DECLARATION.format(aggregate, eventFields))
                        .and(MEMBERS_ASSIGNMENT, STATE_BASED_ASSIGNMENT.format(aggregate, eventFields));
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
