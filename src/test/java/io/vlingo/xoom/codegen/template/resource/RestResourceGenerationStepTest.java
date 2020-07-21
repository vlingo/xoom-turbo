// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.resource;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.resource.RestResourceGenerationStep;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.vlingo.xoom.codegen.CodeGenerationParameter.*;

public class RestResourceGenerationStepTest {

    @Test
    public void testRestResourceGeneration() {
        final CodeGenerationContext context = CodeGenerationContext.empty();
        context.onParameters(loadProperties());
        new RestResourceGenerationStep().process(context);

        System.out.println(context.contents().get(0).file.getAbsolutePath());
        Assert.assertEquals(2, context.contents().size());
        Assert.assertEquals("OrderResource.java", context.contents().get(0).file.getName());
        Assert.assertEquals("ProductResource.java", context.contents().get(1).file.getName());
        Assert.assertTrue(context.contents().get(0).text.contains("class OrderResource"));
        Assert.assertTrue(context.contents().get(0).text.contains("package com.company.context.logistics.resource;"));
        Assert.assertTrue(context.contents().get(1).text.contains("class ProductResource"));
        Assert.assertTrue(context.contents().get(1).text.contains("package com.company.context.logistics.resource;"));
    }

    private Map<CodeGenerationParameter, String> loadProperties() {
        final Map<CodeGenerationParameter, String> parameters = new HashMap<>();
        parameters.put(PACKAGE, "com.company.context.logistics");
        parameters.put(APPLICATION_NAME, "logistics");
        parameters.put(REST_RESOURCES, "Order;Product");
        parameters.put(TARGET_FOLDER, "/Projects/");
        return parameters;
    }

}
