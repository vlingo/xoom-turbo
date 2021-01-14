// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.exchange;

import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameter;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ExchangeReceiverHolderTemplateDataTest {

    @Test
    public void testThatTemplateParametersAreMapped() {
        final List<TemplateData> data =
                ExchangeReceiverHolderTemplateData.from("io.vlingo.xoomapp.infrastructure.exchange",
                        CodeGenerationParametersBuilder.threeExchanges(),
                        Arrays.asList(ContentBuilder.authorDataObjectContent()));

        Assert.assertEquals(1, data.size());

        final TemplateParameters parameters = data.get(0).parameters();

        Assert.assertEquals("Author", parameters.find(TemplateParameter.AGGREGATE_PROTOCOL_NAME));
        Assert.assertEquals("io.vlingo.xoomapp.infrastructure.exchange", parameters.find(TemplateParameter.PACKAGE_NAME));
        Assert.assertEquals("AuthorExchangeReceivers", parameters.find(TemplateParameter.EXCHANGE_RECEIVER_HOLDER_NAME));

        final List<ExchangeReceiversParameter> receiversParameter = parameters.find(TemplateParameter.EXCHANGE_RECEIVERS);

        Assert.assertEquals(3, receiversParameter.size());
        Assert.assertTrue(receiversParameter.stream().allMatch(param -> param.getLocalTypeName().equals("AuthorData")));
        Assert.assertTrue(receiversParameter.stream().anyMatch(param -> param.getSchemaTypeName().equals("OtherAggregateDefined")));
        Assert.assertTrue(receiversParameter.stream().anyMatch(param -> param.getSchemaTypeName().equals("OtherAggregateUpdated")));
        Assert.assertTrue(receiversParameter.stream().anyMatch(param -> param.getSchemaTypeName().equals("OtherAggregateRemoved")));
    }

}
