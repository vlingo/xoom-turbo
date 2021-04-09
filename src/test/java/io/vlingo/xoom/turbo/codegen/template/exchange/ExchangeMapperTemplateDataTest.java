// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
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

import java.util.Arrays;
import java.util.List;

import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.*;

public class ExchangeMapperTemplateDataTest {

    @Test
    public void testThatTemplateParametersAreMapped() {
        final List<TemplateData> mappersData =
                ExchangeMapperTemplateData.from("io.vlingo.xoomapp.infrastructure.exchange",
                        CodeGenerationParametersBuilder.threeExchanges(),
                        Arrays.asList(ContentBuilder.authorDataObjectContent()));

        Assert.assertEquals(2, mappersData.size());

        final TemplateParameters consumerMapperParameters =
                mappersData.stream().map(data -> data.parameters())
                        .filter(params -> params.find(EXCHANGE_MAPPER_NAME).equals("AuthorDataMapper"))
                        .findFirst().get();

        Assert.assertEquals("AuthorData", consumerMapperParameters.find(LOCAL_TYPE_NAME));
        Assert.assertEquals("io.vlingo.xoomapp.infrastructure.exchange", consumerMapperParameters.find(PACKAGE_NAME));
        Assert.assertTrue(consumerMapperParameters.hasImport("io.vlingo.xoomapp.infrastructure.AuthorData"));

        final TemplateParameters producerMapperParameters =
                mappersData.stream().map(data -> data.parameters())
                        .filter(params -> params.find(EXCHANGE_MAPPER_NAME).equals("DomainEventMapper"))
                        .findFirst().get();

        Assert.assertEquals("io.vlingo.xoomapp.infrastructure.exchange", producerMapperParameters.find(PACKAGE_NAME));
    }

}
