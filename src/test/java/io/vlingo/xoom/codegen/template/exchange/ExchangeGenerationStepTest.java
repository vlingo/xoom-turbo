// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.exchange;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameters;
import org.junit.Assert;
import org.junit.Test;

import static io.vlingo.xoom.codegen.parameter.Label.PACKAGE;
import static java.util.stream.Collectors.toList;

public class ExchangeGenerationStepTest {

    @Test
    public void testThatExchangeCodeIsGenerated() {
        final CodeGenerationParameters parameters =
                CodeGenerationParameters.empty()
                        .addAll(CodeGenerationParametersBuilder.threeExchanges().collect(toList()));

        final CodeGenerationContext context =
                CodeGenerationContext.with(parameters).with(PACKAGE, "io.vlingo.xoomapp");

        context.contents(ContentBuilder.aggregateAndEvents().toArray(new Content[]{}));

        new ExchangeGenerationStep().process(context);

        Assert.assertEquals(17, context.contents().size());

        final Content authorExchangeReceivers =
                context.contents().stream().filter(content -> content.retrieveClassName().equals("AuthorExchangeReceivers"))
                        .findFirst().get();


        authorExchangeReceivers.contains("static class OtherAggregateDefined ");
        authorExchangeReceivers.contains("static class OtherAggregateUpdated ");
        authorExchangeReceivers.contains("static class OtherAggregateRemoved ");
        authorExchangeReceivers.contains("Author.withName(data.name)");
        authorExchangeReceivers.contains("stage.actorOf(Author.class, stage.addressFactory().from(data.id), AuthorEntity.class)");
        authorExchangeReceivers.contains(".andFinallyConsume(author -> author.block(data.rank)");
    }

}
