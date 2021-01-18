// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.schemata;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.exchange.ExchangeRole;
import io.vlingo.xoom.codegen.template.model.AggregateDetail;
import io.vlingo.xoom.codegen.template.model.StateFieldDetail;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateParameter.*;

public class SchemataSpecificationTemplateData extends TemplateData {

    private final TemplateParameters parameters;

    public static List<TemplateData> from(final List<CodeGenerationParameter> exchanges) {
        final Predicate<CodeGenerationParameter> onlyProducers =
                exchange -> exchange.retrieveRelatedValue(ROLE, ExchangeRole::of).isProducer();

        return exchanges.stream().filter(onlyProducers).flatMap(exchange -> exchange.retrieveAllRelated(DOMAIN_EVENT))
                .map(SchemataSpecificationTemplateData::new).collect(Collectors.toList());
    }

    public SchemataSpecificationTemplateData(final CodeGenerationParameter outgoingEvent) {
        this.parameters =
                TemplateParameters.with(SCHEMATA_SPECIFICATION_NAME, outgoingEvent.value)
                        .and(FIELD_DECLARATIONS, generateFieldDeclarations(outgoingEvent))
                        .and(SCHEMATA_FILE, true);
    }

    private List<String> generateFieldDeclarations(final CodeGenerationParameter outgoingEvent) {
        final CodeGenerationParameter aggregate = outgoingEvent.parent(AGGREGATE);
        final CodeGenerationParameter event = AggregateDetail.eventWithName(aggregate, outgoingEvent.value);
        return event.retrieveAllRelated(STATE_FIELD).map(field -> {
            final String fieldType = StateFieldDetail.typeOf(aggregate, field.value);
            return fieldType.toLowerCase() + " " + field.value;
        }).collect(Collectors.toList());
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public TemplateStandard standard() {
        return TemplateStandard.SCHEMATA_SPECIFICATION;
    }
}
