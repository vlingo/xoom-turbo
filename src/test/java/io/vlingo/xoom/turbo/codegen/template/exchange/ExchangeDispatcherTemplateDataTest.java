// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.codegen.template.exchange;

import io.vlingo.xoom.turbo.codegen.template.TemplateData;
import io.vlingo.xoom.turbo.codegen.template.TemplateParameters;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.PACKAGE_NAME;
import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.PRODUCER_EXCHANGES;

public class ExchangeDispatcherTemplateDataTest {

    @Test
    public void testThatTemplateParametersAreMapped() {
        final TemplateData dispatcherTemplateData =
                ExchangeDispatcherTemplateData.from("io.vlingo.xoomapp.infrastructure.exchange",
                        CodeGenerationParametersBuilder.threeExchanges(), ContentBuilder.contents());

        final TemplateParameters parameters = dispatcherTemplateData.parameters();

        Assert.assertEquals("io.vlingo.xoomapp.infrastructure.exchange", parameters.find(PACKAGE_NAME));
        Assert.assertTrue(parameters.hasImport("io.vlingo.xoomapp.model.author.AuthorRated"));
        Assert.assertTrue(parameters.hasImport("io.vlingo.xoomapp.model.author.AuthorBlocked"));
        Assert.assertTrue(parameters.hasImport("io.vlingo.xoomapp.model.book.BookSoldOut"));
        Assert.assertTrue(parameters.hasImport("io.vlingo.xoomapp.model.book.BookPurchased"));

        final List<ProducerExchange> exchanges = parameters.find(PRODUCER_EXCHANGES);

        Assert.assertEquals(2, exchanges.size());

        final ProducerExchange authorExchange =
            exchanges.stream().filter(exchange -> exchange.getName().equals("author-exchange")).findFirst().get();

        Assert.assertEquals(2, authorExchange.getEvents().size());
        Assert.assertTrue(authorExchange.getEvents().contains("AuthorRated"));
        Assert.assertTrue(authorExchange.getEvents().contains("AuthorBlocked"));


    }

}
