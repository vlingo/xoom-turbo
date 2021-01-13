// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.exchange;

import io.vlingo.lattice.model.IdentifiedDomainEvent;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateFile;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.DATA_OBJECT;

public class ExchangeAdapterTemplateDataTest {

    @Test
    public void testThatExchangeAdapterTemplateDataAreMapped() {
        final Content authorDataObject =
                Content.with(DATA_OBJECT, new TemplateFile("", "AuthorData.java"), null, null, AUTHOR_DATA_CONTENT_TEXT);

        final List<TemplateData> data =
                ExchangeAdapterTemplateData.from("io.vlingo.xoomapp.infrastructure.exchange",
                        buildGenerationParameters(), Arrays.asList(authorDataObject));

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

    private Stream<CodeGenerationParameter> buildGenerationParameters() {
        final CodeGenerationParameter otherAppExchange =
                CodeGenerationParameter.of(Label.EXCHANGE, "otherapp-exchange")
                        .relate(Label.ROLE, ExchangeRole.CONSUMER)
                        .relate(Label.SCHEMA, "vlingo:xoom:io.vlingo.otherapp:OtherAggregateDefined:0.0.1")
                        .relate(Label.SCHEMA, "vlingo:xoom:io.vlingo.otherapp:OtherAggregateUpdated:0.0.2")
                        .relate(Label.SCHEMA, "vlingo:xoom:io.vlingo.otherapp:OtherAggregateRemoved:0.0.3");

        final CodeGenerationParameter authorExchange =
                CodeGenerationParameter.of(Label.EXCHANGE, "author-exchange")
                        .relate(Label.ROLE, ExchangeRole.PRODUCER)
                        .relate(Label.SCHEMA_GROUP, "vlingo:xoom:io.vlingo.xoomapp")
                        .relate(Label.DOMAIN_EVENT, "AuthorBlocked")
                        .relate(Label.DOMAIN_EVENT, "AuthorRated");

        final CodeGenerationParameter authorAggregate =
                CodeGenerationParameter.of(Label.AGGREGATE, "Author")
                        .relate(otherAppExchange)
                        .relate(authorExchange);

        final CodeGenerationParameter bookExchange =
                CodeGenerationParameter.of(Label.EXCHANGE, "book-exchange")
                        .relate(Label.ROLE, ExchangeRole.PRODUCER)
                        .relate(Label.SCHEMA_GROUP, "vlingo:xoom:io.vlingo.xoomapp")
                        .relate(Label.DOMAIN_EVENT, "BookSoldOut")
                        .relate(Label.DOMAIN_EVENT, "BookPurchased");

        final CodeGenerationParameter bookAggregate =
                CodeGenerationParameter.of(Label.AGGREGATE, "Book")
                        .relate(bookExchange);

        return Stream.of(authorAggregate, bookAggregate);
    }

    private static final String AUTHOR_DATA_CONTENT_TEXT =
            "package io.vlingo.xoomapp.infrastructure; \\n" +
                    "public class AuthorData { \\n" +
                    "... \\n" +
                    "}";

}
