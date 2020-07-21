// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.resource;

import io.vlingo.xoom.codegen.template.TemplateData;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static io.vlingo.xoom.codegen.template.TemplateParameter.PACKAGE_NAME;
import static io.vlingo.xoom.codegen.template.TemplateParameter.REST_RESOURCE_NAME;

public class RestResourceTemplateDataFactoryTest {

    @Test
    public void testRestResourceTemplateDataBuild() {
        final String basePackage = "io.vlingo.xoomapp";
        final String restResourcesData = "Author;Book";

        final List<TemplateData> templateData =
                RestResourceTemplateDataFactory.build(basePackage, restResourcesData);

        Assert.assertEquals(2, templateData.size());

        Assert.assertEquals("AuthorResource", templateData.get(0).parameters().find(REST_RESOURCE_NAME));
        Assert.assertEquals("io.vlingo.xoomapp.resource", templateData.get(0).parameters().find(PACKAGE_NAME));
        Assert.assertEquals("AuthorResource.java", templateData.get(0).filename());

        Assert.assertEquals("BookResource", templateData.get(1).parameters().find(REST_RESOURCE_NAME));
        Assert.assertEquals("io.vlingo.xoomapp.resource", templateData.get(1).parameters().find(PACKAGE_NAME));
        Assert.assertEquals("BookResource.java", templateData.get(1).filename());
    }

}
