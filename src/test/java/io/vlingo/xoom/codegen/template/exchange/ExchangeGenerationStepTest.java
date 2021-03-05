// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.exchange;

import io.vlingo.xoom.TextExpectation;
import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static io.vlingo.xoom.codegen.parameter.Label.PACKAGE;
import static java.util.stream.Collectors.toList;

public class ExchangeGenerationStepTest {

    @Test
    public void testThatExchangeCodeIsGenerated() throws IOException {
        final CodeGenerationParameters parameters =
                CodeGenerationParameters.empty()
                        .addAll(CodeGenerationParametersBuilder.threeExchanges().collect(toList()));

        final CodeGenerationContext context =
                CodeGenerationContext.with(parameters).with(PACKAGE, "io.vlingo.xoomapp");

        context.contents(ContentBuilder.contents().toArray(new Content[]{}));

        new ExchangeGenerationStep().process(context);

        Assert.assertEquals(19, context.contents().size());

        final Content authorExchangeReceivers =
                context.findContent(TemplateStandard.EXCHANGE_RECEIVER_HOLDER, "AuthorExchangeReceivers");

        authorExchangeReceivers.contains(TextExpectation.onJava().read("author-exchange-receivers"));
    }
}
