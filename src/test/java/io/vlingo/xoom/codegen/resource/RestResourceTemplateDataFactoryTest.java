// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.resource;

import io.vlingo.xoom.OperatingSystem;
import io.vlingo.xoom.codegen.TemplateData;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;

import static io.vlingo.xoom.codegen.CodeTemplateParameter.PACKAGE_NAME;
import static io.vlingo.xoom.codegen.CodeTemplateParameter.REST_RESOURCE_NAME;

public class RestResourceTemplateDataFactoryTest {

    private static final String PROJECT_PATH =
            OperatingSystem.detect().isWindows() ?
                    Paths.get("D:\\projects", "xoom-app").toString() :
                    Paths.get("/home", "xoom-app").toString();

    private static final String RESOURCE_PACKAGE_PATH =
            Paths.get(PROJECT_PATH, "src", "main", "java", "io", "vlingo", "xoomapp", "resource").toString();

    @Test
    public void testRestResourceTemplateDataBuild() {
        final String basePackage = "io.vlingo.xoomapp";
        final String restResourcesData = "Author;Book";

        final List<TemplateData> templateData =
                RestResourceTemplateDataFactory.build(basePackage, PROJECT_PATH, restResourcesData);

        Assert.assertEquals(2, templateData.size());

        Assert.assertEquals("AuthorResource", templateData.get(0).parameters().find(REST_RESOURCE_NAME));
        Assert.assertEquals("io.vlingo.xoomapp.resource", templateData.get(0).parameters().find(PACKAGE_NAME));
        Assert.assertEquals(Paths.get(RESOURCE_PACKAGE_PATH, "AuthorResource.java").toString(), templateData.get(0).file().getAbsolutePath());

        Assert.assertEquals("BookResource", templateData.get(1).parameters().find(REST_RESOURCE_NAME));
        Assert.assertEquals("io.vlingo.xoomapp.resource", templateData.get(1).parameters().find(PACKAGE_NAME));
        Assert.assertEquals(Paths.get(RESOURCE_PACKAGE_PATH, "BookResource.java").toString(), templateData.get(1).file().getAbsolutePath());
    }

}
