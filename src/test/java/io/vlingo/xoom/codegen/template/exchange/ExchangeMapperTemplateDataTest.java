// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.exchange;

import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;

public class ExchangeMapperTemplateDataTest {

    @Test
    public void testThatTemplateParametersAreMapped() {
        final List<TemplateData> data =
                ExchangeMapperTemplateData.from("io.vlingo.xoomapp.infrastructure.exchange",
                        CodeGenerationParametersBuilder.threeExchanges(),
                        Arrays.asList(ContentBuilder.authorDataObjectContent()));

        Assert.assertEquals(1, data.size());

        final TemplateParameters parameters = data.get(0).parameters();

        Assert.assertEquals("AuthorData", parameters.find(LOCAL_TYPE_NAME));
        Assert.assertEquals("AuthorDataMapper", parameters.find(EXCHANGE_MAPPER_NAME));
        Assert.assertEquals("io.vlingo.xoomapp.infrastructure.exchange", parameters.find(PACKAGE_NAME));
        Assert.assertTrue(parameters.hasImport("io.vlingo.xoomapp.infrastructure.AuthorData"));
    }

}
