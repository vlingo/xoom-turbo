// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.exchange;

import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;

public class ExchangeBootstrapTemplateDataTest {

    @Test
    public void testThatTemplateParametersAreMapped() {
        final TemplateData data =
                ExchangeBootstrapTemplateData.from("io.vlingo.xoomapp.infrastructure.exchange",
                        CodeGenerationParametersBuilder.threeExchanges(),
                        Arrays.asList(ContentBuilder.authorDataObjectContent()));

        final TemplateParameters parameters = data.parameters();

        Assert.assertEquals("io.vlingo.xoomapp.infrastructure.exchange", parameters.find(PACKAGE_NAME));
        Assert.assertEquals("authorExchange, bookExchange", parameters.find(PRODUCER_EXCHANGES));

        final List<ExchangesParameter> exchangesParameters = data.parameters().find(EXCHANGES);

        Assert.assertEquals(3, exchangesParameters.size());
        Assert.assertTrue(exchangesParameters.stream().anyMatch(param -> param.getName().equals("author-exchange")));
        Assert.assertTrue(exchangesParameters.stream().anyMatch(param -> param.getName().equals("book-exchange")));
        Assert.assertTrue(exchangesParameters.stream().anyMatch(param -> param.getName().equals("otherapp-exchange")));

        final ExchangesParameter otherAppExchange =
                exchangesParameters.stream().filter(param -> param.getName().equals("otherapp-exchange")).findFirst().get();

        Assert.assertEquals("otherappExchange", otherAppExchange.getVariableName());
        Assert.assertEquals("otherappExchangeSettings", otherAppExchange.getSettingsName());
        Assert.assertEquals(3, otherAppExchange.getCoveys().size());
        Assert.assertTrue(otherAppExchange.getCoveys().stream().allMatch(covey -> covey.getLocalClass().equals("AuthorData")));
        Assert.assertTrue(otherAppExchange.getCoveys().stream().allMatch(convey -> convey.getExternalClass().equals("String")));

        final CoveyParameter schemaRemovedCovey =
                otherAppExchange.getCoveys().stream()
                        .filter(convey -> convey.getReceiverInstantiation().equals("new AuthorExchangeReceivers.OtherAggregateRemoved()"))
                        .findFirst().get();

        Assert.assertEquals("new AuthorConsumerAdapter(\"vlingo:xoom:io.vlingo.otherapp:OtherAggregateRemoved:0.0.3\")", schemaRemovedCovey.getAdapterInstantiation());

        final ExchangesParameter authorExchange =
                exchangesParameters.stream().filter(param -> param.getName().equals("author-exchange")).findFirst().get();

        Assert.assertEquals("authorExchange", authorExchange.getVariableName());
        Assert.assertEquals("authorExchangeSettings", authorExchange.getSettingsName());
        Assert.assertEquals(1, authorExchange.getCoveys().size());

        final CoveyParameter authorExchangeCovey = authorExchange.getCoveys().stream().findFirst().get();

        Assert.assertEquals("IdentifiedDomainEvent", authorExchangeCovey.getLocalClass());
        Assert.assertEquals("IdentifiedDomainEvent", authorExchangeCovey.getExternalClass());
        Assert.assertEquals("new AuthorProducerAdapter()",authorExchangeCovey.getAdapterInstantiation());
        Assert.assertEquals("received -> {}", authorExchangeCovey.getReceiverInstantiation());
    }

}
