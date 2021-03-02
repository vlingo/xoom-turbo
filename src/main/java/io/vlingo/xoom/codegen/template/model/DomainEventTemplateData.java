// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.model;

import io.vlingo.xoom.codegen.language.Language;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.model.formatting.AggregateFieldsFormat;

import java.util.List;
import java.util.stream.Collectors;

import static io.vlingo.xoom.codegen.parameter.Label.STATE_FIELD;
import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE_STATE;
import static io.vlingo.xoom.codegen.template.TemplateStandard.DOMAIN_EVENT;
import static io.vlingo.xoom.codegen.template.model.formatting.AggregateFieldsFormat.Style.MEMBER_DECLARATION;
import static io.vlingo.xoom.codegen.template.model.formatting.AggregateFieldsFormat.Style.STATE_BASED_ASSIGNMENT;

public class DomainEventTemplateData extends TemplateData {

    private final String name;
    private final TemplateParameters parameters;

    public static List<TemplateData> from(final String packageName,
                                          final Language language,
                                          final CodeGenerationParameter aggregate) {
        return aggregate.retrieveAllRelated(Label.DOMAIN_EVENT).map(event ->
                new DomainEventTemplateData(packageName, language, event, aggregate))
                .collect(Collectors.toList());
    }

    private DomainEventTemplateData(final String packageName,
                                    final Language language,
                                    final CodeGenerationParameter event,
                                    final CodeGenerationParameter aggregate) {
        this.name = event.value;
        this.parameters =
                TemplateParameters.with(PACKAGE_NAME, packageName).and(DOMAIN_EVENT_NAME, name)
                        .and(STATE_NAME, AGGREGATE_STATE.resolveClassname(aggregate.value))
                        .and(MEMBERS, AggregateFieldsFormat.format(MEMBER_DECLARATION, language, aggregate, event.retrieveAllRelated(STATE_FIELD)))
                        .and(MEMBERS_ASSIGNMENT, AggregateFieldsFormat.format(STATE_BASED_ASSIGNMENT, language, aggregate, event.retrieveAllRelated(STATE_FIELD)));
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
