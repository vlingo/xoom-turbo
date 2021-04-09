// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.codegen.template.exchange;

import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;
import io.vlingo.xoom.turbo.codegen.template.TemplateData;
import io.vlingo.xoom.turbo.codegen.template.TemplateParameters;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.*;

public class ExchangeAdapterTemplateDataTest {

    @Test
    public void testThatTemplateParametersAreMapped() {
        final List<TemplateData> data =
                ExchangeAdapterTemplateData.from("io.vlingo.xoomapp.infrastructure.exchange",
                        CodeGenerationParametersBuilder.threeExchanges(),
                        Arrays.asList(ContentBuilder.authorDataObjectContent()));

        Assert.assertEquals(3, data.size());

        final Predicate<TemplateParameters> filterAuthorConsumerAdapter =
                parameters -> parameters.find(EXCHANGE_ADAPTER_NAME).equals("AuthorConsumerAdapter");

        final TemplateParameters authorConsumerAdapterParameters =
                data.stream().map(TemplateData::parameters).filter(filterAuthorConsumerAdapter).findFirst().get();

        Assert.assertEquals("io.vlingo.xoomapp.infrastructure.exchange", authorConsumerAdapterParameters.find(PACKAGE_NAME));
        Assert.assertEquals("Author", authorConsumerAdapterParameters.find(AGGREGATE_PROTOCOL_NAME));
        Assert.assertEquals("AuthorData", authorConsumerAdapterParameters.find(LOCAL_TYPE_NAME));
        Assert.assertEquals("CONSUMER", authorConsumerAdapterParameters.<ExchangeRole>find(EXCHANGE_ROLE).name());
        Assert.assertEquals("AuthorDataMapper", authorConsumerAdapterParameters.find(EXCHANGE_MAPPER_NAME));
        Assert.assertTrue(authorConsumerAdapterParameters.hasImport("io.vlingo.xoomapp.infrastructure.AuthorData"));

        final Predicate<TemplateParameters> filterBookProducerAdapter =
                parameters -> parameters.find(EXCHANGE_ADAPTER_NAME).equals("BookProducerAdapter");

        final TemplateParameters bookProduceAdapterParameters =
                data.stream().map(TemplateData::parameters).filter(filterBookProducerAdapter).findFirst().get();

        Assert.assertEquals("io.vlingo.xoomapp.infrastructure.exchange", bookProduceAdapterParameters.find(PACKAGE_NAME));
        Assert.assertEquals("Book", bookProduceAdapterParameters.find(AGGREGATE_PROTOCOL_NAME));
        Assert.assertEquals("vlingo:xoom:io.vlingo.xoomapp", bookProduceAdapterParameters.find(SCHEMA_GROUP_NAME));
        Assert.assertEquals("PRODUCER", bookProduceAdapterParameters.<ExchangeRole>find(EXCHANGE_ROLE).name());
        Assert.assertTrue(bookProduceAdapterParameters.hasImport(IdentifiedDomainEvent.class.getCanonicalName()));
    }


}
