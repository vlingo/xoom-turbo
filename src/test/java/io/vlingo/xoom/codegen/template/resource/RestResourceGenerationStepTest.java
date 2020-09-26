// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.resource;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.parameter.Label;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.vlingo.xoom.codegen.parameter.Label.*;

public class RestResourceGenerationStepTest {

    @Test
    public void testRestResourceGeneration() {
        final CodeGenerationContext context = CodeGenerationContext.with(loadProperties());
        new RestResourceGenerationStep().process(context);

        Assert.assertEquals(2, context.contents().size());
        Assert.assertEquals("OrderResource", context.contents().get(0).retrieveClassName());
        Assert.assertEquals("ProductResource", context.contents().get(1).retrieveClassName());
        Assert.assertTrue(context.contents().get(0).contains("class OrderResource"));
        Assert.assertTrue(context.contents().get(0).contains("package com.company.context.logistics.resource;"));
        Assert.assertTrue(context.contents().get(1).contains("class ProductResource"));
        Assert.assertTrue(context.contents().get(1).contains("package com.company.context.logistics.resource;"));
    }

    private Map<Label, String> loadProperties() {
        final Map<Label, String> parameters = new HashMap<>();
        parameters.put(PACKAGE, "com.company.context.logistics");
        parameters.put(APPLICATION_NAME, "logistics");
        parameters.put(REST_RESOURCES, "Order;Product");
        parameters.put(TARGET_FOLDER, "/Projects/");
        return parameters;
    }

}
