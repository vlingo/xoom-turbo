// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.exchange;

import io.vlingo.lattice.model.IdentifiedDomainEvent;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.TemplateParameters;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.ROLE;
import static io.vlingo.xoom.codegen.parameter.Label.SCHEMA;
import static io.vlingo.xoom.codegen.template.TemplateParameter.AGGREGATE_PROTOCOL_NAME;
import static io.vlingo.xoom.codegen.template.TemplateParameter.EXCHANGE_ROLE;
import static io.vlingo.xoom.codegen.template.TemplateStandard.*;

public class CoveyParameter {

    private final String localClass;
    private final String externalClass;
    private final String adapterInstantiation;
    private final String receiverInstantiation;

    public static Set<CoveyParameter> from(final List<CodeGenerationParameter> exchanges) {
        final Predicate<CodeGenerationParameter> onlyConsumers =
                exchange -> exchange.retrieveRelatedValue(ROLE, ExchangeRole::of).isConsumer();

        final Predicate<CodeGenerationParameter> onlyProducers =
                exchange -> exchange.retrieveRelatedValue(ROLE, ExchangeRole::of).isProducer();

        final Set<CoveyParameter> consumerCoveys =
                exchanges.stream().filter(onlyConsumers).flatMap(exchange -> exchange.retrieveAllRelated(SCHEMA))
                        .map(schema -> new CoveyParameter(schema.parent(), schema)).collect(Collectors.toSet());

        final Set<CoveyParameter> producerCoveys =
                exchanges.stream().filter(onlyProducers).map(CoveyParameter::new).collect(Collectors.toSet());

        return Stream.of(consumerCoveys, producerCoveys).flatMap(Set::stream).collect(Collectors.toSet());
    }

    private CoveyParameter(final CodeGenerationParameter consumerExchange,
                           final CodeGenerationParameter schema) {
        this.externalClass = String.class.getSimpleName();
        this.localClass = DATA_OBJECT.resolveClassname(consumerExchange.parent().value);
        this.adapterInstantiation = resolveConsumerAdapterInstantiation(consumerExchange, schema);
        this.receiverInstantiation = String.format("new %s()", resolveReceiverName(consumerExchange, schema));
    }

    private CoveyParameter(final CodeGenerationParameter producerExchange) {
        this.localClass = String.class.getSimpleName();
        this.externalClass = IdentifiedDomainEvent.class.getSimpleName();
        this.adapterInstantiation = String.format("new %s()", resolveAdapterName(producerExchange));
        this.receiverInstantiation = "received -> {}";
    }

    private String resolveConsumerAdapterInstantiation(final CodeGenerationParameter consumerExchange,
                                                       final CodeGenerationParameter schema) {
        return String.format("new %s(\"%s\")", resolveAdapterName(consumerExchange), schema.value);
    }

    private String resolveReceiverName(final CodeGenerationParameter consumerExchange,
                                       final CodeGenerationParameter schema) {
        final String schemaTypeName = Formatter.formatSchemaTypeName(schema);

        final TemplateParameters aggregateProtocolName =
                TemplateParameters.with(AGGREGATE_PROTOCOL_NAME, consumerExchange.parent().value);

        final String holderName =
                EXCHANGE_RECEIVER_HOLDER.resolveClassname(aggregateProtocolName);

        return String.format("%s.%s", holderName, schemaTypeName);
    }

    private String resolveAdapterName(final CodeGenerationParameter exchange) {
        final TemplateParameters parameters =
                TemplateParameters.with(AGGREGATE_PROTOCOL_NAME, exchange.parent().value)
                        .and(EXCHANGE_ROLE, exchange.retrieveRelatedValue(ROLE, ExchangeRole::of));

        return EXCHANGE_ADAPTER.resolveClassname(parameters);
    }

    public String getLocalClass() {
        return localClass;
    }

    public String getAdapterInstantiation() {
        return adapterInstantiation;
    }

    public String getExternalClass() {
        return externalClass;
    }

    public String getReceiverInstantiation() {
        return receiverInstantiation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoveyParameter that = (CoveyParameter) o;
        return Objects.equals(localClass, that.localClass) &&
                Objects.equals(adapterInstantiation, that.adapterInstantiation) &&
                Objects.equals(externalClass, that.externalClass) &&
                Objects.equals(receiverInstantiation, that.receiverInstantiation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(localClass, adapterInstantiation, externalClass, receiverInstantiation);
    }
}
