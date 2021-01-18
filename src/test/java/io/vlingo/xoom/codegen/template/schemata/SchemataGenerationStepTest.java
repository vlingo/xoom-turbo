// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.schemata;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameters;
import io.vlingo.xoom.codegen.template.exchange.CodeGenerationParametersBuilder;
import org.junit.Assert;
import org.junit.Test;

import static java.util.stream.Collectors.toList;

public class SchemataGenerationStepTest {

    @Test
    public void testThatSpecificationAndPluginConfigAreGenerated() {
        final CodeGenerationParameters parameters =
                CodeGenerationParameters.empty()
                        .addAll(CodeGenerationParametersBuilder.threeExchanges().collect(toList()));

        final CodeGenerationContext context =
                CodeGenerationContext.with(parameters);

        new SchemataGenerationStep().process(context);

        Assert.assertEquals(5, context.contents().size());

        final Content authorRatedSpecification =
                context.contents().stream().filter(content -> content.contains("event AuthorRated {"))
                .findFirst().get();

        Assert.assertTrue(authorRatedSpecification.contains("string id"));
        Assert.assertTrue(authorRatedSpecification.contains("int rank"));
        Assert.assertTrue(authorRatedSpecification.contains("version semanticVersion"));
    }
}
