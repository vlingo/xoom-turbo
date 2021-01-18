// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.schemata;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameter;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.exchange.ExchangeRole;

import java.util.List;
import java.util.stream.Collectors;

import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.exchange.ExchangeRole.CONSUMER;
import static io.vlingo.xoom.codegen.template.exchange.ExchangeRole.PRODUCER;

public class SchemataPluginTemplateData extends TemplateData {

    private final TemplateParameters parameters;

    public SchemataPluginTemplateData(final List<CodeGenerationParameter> exchanges) {
        final String schemaGroup = retrieveSchemaGroup(exchanges);

        this.parameters =
                TemplateParameters.with(TemplateParameter.POM_SECTION, true)
                        .and(TemplateParameter.OFFSET, "<plugins>")
                        .and(TemplateParameter.PRODUCER_SCHEMAS, retrieveProducerSchemas(schemaGroup, exchanges))
                        .and(TemplateParameter.CONSUMER_SCHEMAS, retrieveConsumerSchemas(exchanges))
                        .and(TemplateParameter.PRODUCER_ORGANIZATION, retrieveProducerOrganization(schemaGroup))
                        .and(TemplateParameter.PRODUCER_UNIT, retrieveProducerUnit(schemaGroup))
                        .and(TemplateParameter.HAS_PRODUCER_EXCHANGE, hasExchangesWithRole(PRODUCER, exchanges))
                        .and(TemplateParameter.HAS_CONSUMER_EXCHANGE, hasExchangesWithRole(CONSUMER, exchanges));
    }

    private String retrieveProducerUnit(final String schemaGroup) {
        return retrieveSchemaGroupPart(1, schemaGroup);
    }

    private String retrieveProducerOrganization(final String schemaGroup) {
        return retrieveSchemaGroupPart(0, schemaGroup);
    }

    private String retrieveSchemaGroupPart(final int part, final String schemaGroup) {
        return schemaGroup.isEmpty() ? "" : schemaGroup.split(":")[part];
    }

    private String retrieveSchemaGroup(final List<CodeGenerationParameter> exchanges) {
        return exchanges.stream().filter(ex -> ex.retrieveRelatedValue(ROLE, ExchangeRole::of).isProducer())
                .map(ex -> ex.retrieveOneRelated(SCHEMA_GROUP).value).findFirst().orElse("");
    }

    private List<SchemaParameter> retrieveConsumerSchemas(final List<CodeGenerationParameter> exchanges) {
        return exchanges.stream().filter(ex -> ex.retrieveRelatedValue(ROLE, ExchangeRole::of).isConsumer())
                .flatMap(ex -> ex.retrieveAllRelated(RECEIVER)).flatMap(ex -> ex.retrieveAllRelated(SCHEMA))
                .map(schema -> new SchemaParameter(schema)).collect(Collectors.toList());
    }

    private List<SchemaParameter> retrieveProducerSchemas(final String schemaGroup, final List<CodeGenerationParameter> exchanges) {
        return exchanges.stream().filter(ex -> ex.retrieveRelatedValue(ROLE, ExchangeRole::of).isProducer())
                .flatMap(ex -> ex.retrieveAllRelated(DOMAIN_EVENT)).map(event -> new SchemaParameter(schemaGroup, event))
                .collect(Collectors.toList());
    }

    private boolean hasExchangesWithRole(final ExchangeRole role, final List<CodeGenerationParameter> exchanges) {
        return exchanges.stream().anyMatch(ex -> ex.retrieveRelatedValue(ROLE, ExchangeRole::of).equals(role));
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public TemplateStandard standard() {
        return TemplateStandard.SCHEMATA_PLUGIN;
    }
}
