// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.bootstrap;

import io.vlingo.xoom.OperatingSystem;
import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateFile;
import io.vlingo.xoom.codegen.template.TemplateParameter;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.projections.ProjectionType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static io.vlingo.xoom.codegen.parameter.Label.APPLICATION_NAME;
import static io.vlingo.xoom.codegen.parameter.Label.BLOCKING_MESSAGING;
import static io.vlingo.xoom.codegen.parameter.Label.STORAGE_TYPE;
import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.*;
import static io.vlingo.xoom.codegen.template.storage.StorageType.STATE_STORE;

public class AnnotatedBootstrapTemplateDataTest {

    @Test
    public void testBootstrapTemplateDataGenerationWithCQRSAndProjections() {
        final Map<Label, String> codeGenerationParameters =
                new HashMap<Label, String>() {{
                    put(PACKAGE, "io.vlingo.xoomapp");
                    put(APPLICATION_NAME, "xoom-app");
                    put(STORAGE_TYPE, STATE_STORE.name());
                    put(CQRS, Boolean.TRUE.toString());
                    put(BLOCKING_MESSAGING, Boolean.FALSE.toString());
                    put(Label.PROJECTION_TYPE, ProjectionType.EVENT_BASED.name());
                    put(ANNOTATIONS, Boolean.TRUE.toString());
                }};

        final CodeGenerationContext context =
                CodeGenerationContext.with(codeGenerationParameters)
                        .addContent(REST_RESOURCE, new TemplateFile(RESOURCE_PACKAGE_PATH, "AuthorResource.java"), AUTHOR_RESOURCE_CONTENT)
                        .addContent(REST_RESOURCE, new TemplateFile(RESOURCE_PACKAGE_PATH, "BookResource.java"), BOOK_RESOURCE_CONTENT)
                        .addContent(STORE_PROVIDER, new TemplateFile(PERSISTENCE_PACKAGE_PATH, "CommandModelStateStoreProvider.java"), COMMAND_MODEL_STORE_PROVIDER_CONTENT)
                        .addContent(STORE_PROVIDER, new TemplateFile(PERSISTENCE_PACKAGE_PATH, "QueryModelStateStoreProvider.java"), QUERY_MODEL_STORE_PROVIDER_CONTENT)
                        .addContent(PROJECTION_DISPATCHER_PROVIDER, new TemplateFile(PERSISTENCE_PACKAGE_PATH, "ProjectionDispatcherProvider.java"), PROJECTION_DISPATCHER_PROVIDER_CONTENT);

        final TemplateParameters parameters =
                BootstrapTemplateData.from(context).parameters();

        Assert.assertEquals(EXPECTED_PACKAGE, parameters.find(PACKAGE_NAME));
        Assert.assertEquals(1, parameters.<Set>find(IMPORTS).size());
        Assert.assertTrue(parameters.hasImport("io.vlingo.xoom.annotation.initializer.ResourceHandlers"));
        Assert.assertEquals("io.vlingo.xoomapp.resource", parameters.find(TemplateParameter.REST_RESOURCE_PACKAGE));
        Assert.assertEquals("xoom-app", parameters.find(TemplateParameter.APPLICATION_NAME));
        Assert.assertEquals(true, parameters.find(USE_PROJECTIONS));
    }

    @Test
    public void testBootstrapTemplateDataGenerationWithoutCQRSAndProjections() {
        final Map<Label, String> codeGenerationParameters =
                new HashMap<Label, String>() {{
                    put(PACKAGE, "io.vlingo.xoomapp");
                    put(APPLICATION_NAME, "xoom-app");
                    put(STORAGE_TYPE, STATE_STORE.name());
                    put(CQRS, Boolean.FALSE.toString());
                    put(Label.PROJECTION_TYPE, ProjectionType.NONE.name());
                    put(ANNOTATIONS, Boolean.TRUE.toString());
                    put(BLOCKING_MESSAGING, Boolean.FALSE.toString());
                }};

        final CodeGenerationContext context =
                CodeGenerationContext.with(codeGenerationParameters)
                        .addContent(REST_RESOURCE, new TemplateFile(RESOURCE_PACKAGE_PATH, "AuthorResource.java"), AUTHOR_RESOURCE_CONTENT)
                        .addContent(STORE_PROVIDER, new TemplateFile(PERSISTENCE_PACKAGE_PATH, "StateStoreProvider.java"), SINGLE_MODEL_STORE_PROVIDER_CONTENT);

        final TemplateParameters parameters =
                BootstrapTemplateData.from(context).parameters();

        Assert.assertEquals(EXPECTED_PACKAGE, parameters.find(PACKAGE_NAME));
        Assert.assertEquals(1, parameters.<Set>find(IMPORTS).size());
        Assert.assertTrue(parameters.hasImport("io.vlingo.xoom.annotation.initializer.ResourceHandlers"));
        Assert.assertEquals("io.vlingo.xoomapp.resource", parameters.find(TemplateParameter.REST_RESOURCE_PACKAGE));
        Assert.assertEquals("xoom-app", parameters.find(TemplateParameter.APPLICATION_NAME));
        Assert.assertEquals(false, parameters.find(USE_PROJECTIONS));
    }

    private static final String EXPECTED_PACKAGE = "io.vlingo.xoomapp.infrastructure";

    private static final String PROJECT_PATH =
            OperatingSystem.detect().isWindows() ?
                    Paths.get("D:\\projects", "xoom-app").toString() :
                    Paths.get("/home", "xoom-app").toString();

    private static final String RESOURCE_PACKAGE_PATH =
            Paths.get(PROJECT_PATH, "src", "main", "java",
                    "io", "vlingo", "xoomapp", "resource").toString();

    private static final String INFRASTRUCTURE_PACKAGE_PATH =
            Paths.get(PROJECT_PATH, "src", "main", "java",
                    "io", "vlingo", "xoomapp", "infrastructure").toString();

    private static final String PERSISTENCE_PACKAGE_PATH =
            Paths.get(INFRASTRUCTURE_PACKAGE_PATH, "persistence").toString();

    private static final String AUTHOR_RESOURCE_CONTENT =
            "package io.vlingo.xoomapp.resource; \\n" +
                    "public class AuthorResource { \\n" +
                    "... \\n" +
                    "}";

    private static final String BOOK_RESOURCE_CONTENT =
            "package io.vlingo.xoomapp.resource; \\n" +
                    "public class BookResource { \\n" +
                    "... \\n" +
                    "}";

    private static final String COMMAND_MODEL_STORE_PROVIDER_CONTENT =
            "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                    "public class CommandModelStateStoreProvider { \\n" +
                    "... \\n" +
                    "}";

    private static final String QUERY_MODEL_STORE_PROVIDER_CONTENT =
            "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                    "public class QueryModelStateStoreProvider { \\n" +
                    "... \\n" +
                    "}";

    private static final String SINGLE_MODEL_STORE_PROVIDER_CONTENT =
            "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                    "public class StateStoreProvider { \\n" +
                    "... \\n" +
                    "}";

    private static final String PROJECTION_DISPATCHER_PROVIDER_CONTENT =
            "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                    "public class ProjectionDispatcherProvider { \\n" +
                    "... \\n" +
                    "}";
}
