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
import io.vlingo.xoom.codegen.template.model.aggregate.AggregateDetail;
import io.vlingo.xoom.codegen.template.model.valueobject.ValueObjectDetail;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.CodeGenerationSetup.DEFAULT_SCHEMA_VERSION;
import static io.vlingo.xoom.codegen.CodeGenerationSetup.EVENT_SCHEMA_CATEGORY;
import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static java.util.stream.Collectors.toList;

public class DomainEventSpecificationTemplateData extends TemplateData {

    private final TemplateParameters parameters;

    public static List<TemplateData> from(final List<CodeGenerationParameter> exchanges) {
        final Predicate<CodeGenerationParameter> onlyProducers =
                exchange -> exchange.retrieveRelatedValue(ROLE, ExchangeRole::of).isProducer();

        final CodeGenerationParameter producerExchange =
                exchanges.stream().filter(onlyProducers).findAny().get();

        final List<CodeGenerationParameter> domainEvents =
                producerExchange.retrieveAllRelated(DOMAIN_EVENT).collect(toList());

        final String schemaGroup = producerExchange.retrieveRelatedValue(SCHEMA_GROUP);

        return domainEvents.stream().map(e -> new DomainEventSpecificationTemplateData(EVENT_SCHEMA_CATEGORY, schemaGroup, e)).collect(toList());
    }

    private DomainEventSpecificationTemplateData(final String schemaCategory,
                                                 final String schemaGroup,
                                                 final CodeGenerationParameter publishedLanguage) {
        this.parameters =
                TemplateParameters.with(SCHEMA_CATEGORY, schemaCategory)
                        .and(SCHEMATA_SPECIFICATION_NAME, publishedLanguage.value)
                        .and(FIELD_DECLARATIONS, generateFieldDeclarations(schemaGroup, publishedLanguage))
                        .and(SCHEMATA_FILE, true);
    }

    private List<String> generateFieldDeclarations(final String schemaGroup, final CodeGenerationParameter publishedLanguage) {
        final CodeGenerationParameter aggregate = publishedLanguage.parent(AGGREGATE);
        final CodeGenerationParameter event = AggregateDetail.eventWithName(aggregate, publishedLanguage.value);
        final Stream<CodeGenerationParameter> stateFields = event.retrieveAllRelated(STATE_FIELD);
        return stateFields.map(field -> resolveFieldType(schemaGroup, field)).collect(toList());
    }

    private String resolveFieldType(final String schemaGroup,
                                    final CodeGenerationParameter field) {
        final String fieldType = field.retrieveRelatedValue(FIELD_TYPE);
        if(ValueObjectDetail.isValueObject(field)) {
            return String.format("%s:%s:%s", schemaGroup, fieldType, DEFAULT_SCHEMA_VERSION) + " " + field.value;
        }
        return fieldType.toLowerCase() + " " + field.value;
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
