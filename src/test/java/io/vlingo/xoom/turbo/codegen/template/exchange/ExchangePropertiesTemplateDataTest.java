// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.codegen.template.exchange;

import io.vlingo.xoom.turbo.codegen.template.TemplateData;
import io.vlingo.xoom.turbo.codegen.template.TemplateParameter;
import io.vlingo.xoom.turbo.codegen.template.TemplateParameters;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ExchangePropertiesTemplateDataTest {

    @Test
    public void testThatTemplateParametersAreMapped() {
        final TemplateData data =
                ExchangePropertiesTemplateData.from(CodeGenerationParametersBuilder.threeExchanges());

        final TemplateParameters parameters = data.parameters();

        Assert.assertTrue(parameters.find(TemplateParameter.RESOURCE_FILE));
        Assert.assertTrue(parameters.<List>find(TemplateParameter.EXCHANGE_NAMES).contains("otherapp-exchange"));
        Assert.assertTrue(parameters.<List>find(TemplateParameter.EXCHANGE_NAMES).contains("author-exchange"));
        Assert.assertTrue(parameters.<List>find(TemplateParameter.EXCHANGE_NAMES).contains("book-exchange"));
        Assert.assertEquals("otherapp-exchange;author-exchange;book-exchange", parameters.find(TemplateParameter.INLINE_EXCHANGE_NAMES));
    }

}
