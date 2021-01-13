// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.exchange;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;

import java.util.stream.Stream;

public class CodeGenerationParametersBuilder {

    public static Stream<CodeGenerationParameter> threeExchanges() {
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

}
