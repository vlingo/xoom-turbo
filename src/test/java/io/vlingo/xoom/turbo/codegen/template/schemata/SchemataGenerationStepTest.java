// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.codegen.template.schemata;

import io.vlingo.xoom.turbo.TextExpectation;
import io.vlingo.xoom.turbo.codegen.CodeGenerationContext;
import io.vlingo.xoom.turbo.codegen.content.Content;
import io.vlingo.xoom.turbo.codegen.parameter.CodeGenerationParameters;
import io.vlingo.xoom.turbo.codegen.template.TemplateStandard;
import io.vlingo.xoom.turbo.codegen.template.exchange.CodeGenerationParametersBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static java.util.stream.Collectors.toList;

public class SchemataGenerationStepTest {

    @Test
    public void testThatSpecificationAndPluginConfigAreGenerated() throws IOException {
        final CodeGenerationParameters parameters =
                CodeGenerationParameters.empty()
                        .addAll(CodeGenerationParametersBuilder.threeExchanges().collect(toList()));

        final CodeGenerationContext context = CodeGenerationContext.with(parameters);

        new SchemataGenerationStep().process(context);

        final Content authorRatedSpecification =
                context.findContent(TemplateStandard.SCHEMATA_SPECIFICATION, "AuthorRated");

        final Content authorBlockedSpecification =
                context.findContent(TemplateStandard.SCHEMATA_SPECIFICATION, "AuthorBlocked");

        final Content bookSoldOutSpecification =
                context.findContent(TemplateStandard.SCHEMATA_SPECIFICATION, "BookSoldOut");

        final Content bookPurchasedSpecification =
                context.findContent(TemplateStandard.SCHEMATA_SPECIFICATION, "BookPurchased");

        final Content nameSpecification =
                context.findContent(TemplateStandard.SCHEMATA_SPECIFICATION, "Name");

        final Content rankSpecification =
                context.findContent(TemplateStandard.SCHEMATA_SPECIFICATION, "Rank");

        final Content classificationSpecification =
                context.findContent(TemplateStandard.SCHEMATA_SPECIFICATION, "Classification");

        final Content classifierSpecification =
                context.findContent(TemplateStandard.SCHEMATA_SPECIFICATION, "Classifier");

        final Content plugin =
                context.findContent(TemplateStandard.SCHEMATA_PLUGIN, "pom");

        Assert.assertEquals(9, context.contents().size());
        Assert.assertTrue(authorRatedSpecification.contains(TextExpectation.onJava().read("author-rated-specification")));
        Assert.assertTrue(authorBlockedSpecification.contains(TextExpectation.onJava().read("author-blocked-specification")));
        Assert.assertTrue(bookSoldOutSpecification.contains(TextExpectation.onJava().read("book-sold-out")));
        Assert.assertTrue(bookPurchasedSpecification.contains(TextExpectation.onJava().read("book-purchased")));
        Assert.assertTrue(nameSpecification.contains(TextExpectation.onJava().read("name-specification")));
        Assert.assertTrue(rankSpecification.contains(TextExpectation.onJava().read("rank-specification")));
        Assert.assertTrue(classificationSpecification.contains(TextExpectation.onJava().read("classification-specification")));
        Assert.assertTrue(classifierSpecification.contains(TextExpectation.onJava().read("classifier-specification")));
    }
}
