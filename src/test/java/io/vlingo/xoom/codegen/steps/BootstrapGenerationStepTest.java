// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.steps;

import io.vlingo.xoom.OperatingSystem;
import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.ProjectionType;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;

import static io.vlingo.xoom.codegen.CodeGenerationParameter.*;
import static io.vlingo.xoom.codegen.CodeTemplateStandard.*;

public class BootstrapGenerationStepTest {

    @Test
    public void testDefaultBootstrapGeneration() {
        final CodeGenerationContext context =
                CodeGenerationContext.empty();

        loadParameters(context, false);
        loadContents(context);

        new BootstrapGenerationStep().process(context);

        Assert.assertEquals(6, context.contents().size());
        Assert.assertEquals("Bootstrap.java", context.contents().get(5).file.getName());
        Assert.assertTrue(context.contents().get(5).text.contains("final ProjectionDispatcherProvider projectionDispatcherProvider"));
        Assert.assertTrue(context.contents().get(5).text.contains("CommandModelStateStoreProvider.using(stage, statefulTypeRegistry, projectionDispatcherProvider.storeDispatcher)"));
        Assert.assertTrue(context.contents().get(5).text.contains("QueryModelStateStoreProvider.using(stage, statefulTypeRegistry)"));
        Assert.assertTrue(context.contents().get(5).text.contains("final AuthorResource authorResource = new AuthorResource(stage);"));
        Assert.assertTrue(context.contents().get(5).text.contains("final BookResource bookResource = new BookResource(stage);"));
        Assert.assertTrue(context.contents().get(5).text.contains("authorResource.routes(),"));
        Assert.assertTrue(context.contents().get(5).text.contains("bookResource.routes()"));
        Assert.assertFalse(context.contents().get(5).text.contains("bookResource.routes(),"));
    }

    @Test
    public void testAnnotatedBootstrapGeneration() {
        final CodeGenerationContext context =
                CodeGenerationContext.empty();

        loadParameters(context, true);
        loadContents(context);

        new BootstrapGenerationStep().process(context);

        Assert.assertEquals(6, context.contents().size());
        Assert.assertEquals("Bootstrap.java", context.contents().get(5).file.getName());
        Assert.assertTrue(context.contents().get(5).text.contains("final ProjectionDispatcherProvider projectionDispatcherProvider"));
        Assert.assertTrue(context.contents().get(5).text.contains("CommandModelStateStoreProvider.using(stage, statefulTypeRegistry, projectionDispatcherProvider.storeDispatcher)"));
        Assert.assertTrue(context.contents().get(5).text.contains("QueryModelStateStoreProvider.using(stage, statefulTypeRegistry)"));
        Assert.assertTrue(context.contents().get(5).text.contains("@ResourceHandlers(packages = \"io.vlingo.xoomapp.resource\")"));
    }

    private void loadParameters(final CodeGenerationContext context, final Boolean useAnnotation) {
        context.with(PACKAGE, "io.vlingo").with(APPLICATION_NAME, "xoomapp").with(CQRS, "true")
                .with(TARGET_FOLDER, HOME_DIRECTORY).with(STORAGE_TYPE, "STATE_STORE")
                .with(PROJECTIONS, ProjectionType.OPERATION_BASED.name())
                .with(ANNOTATIONS, useAnnotation.toString());
    }

    private void loadContents(final CodeGenerationContext context) {
        context.addContent(REST_RESOURCE, new File(Paths.get(RESOURCE_PACKAGE_PATH, "AuthorResource.java").toString()), AUTHOR_RESOURCE_CONTENT);
        context.addContent(REST_RESOURCE, new File(Paths.get(RESOURCE_PACKAGE_PATH, "BookResource.java").toString()), BOOK_RESOURCE_CONTENT);
        context.addContent(STORE_PROVIDER, new File(Paths.get(PERSISTENCE_PACKAGE_PATH, "CommandModelStateStoreProvider.java").toString()), COMMAND_MODEL_STORE_PROVIDER_CONTENT);
        context.addContent(STORE_PROVIDER, new File(Paths.get(PERSISTENCE_PACKAGE_PATH, "QueryModelStateStoreProvider.java").toString()), QUERY_MODEL_STORE_PROVIDER_CONTENT);
        context.addContent(PROJECTION_DISPATCHER_PROVIDER, new File(Paths.get(PERSISTENCE_PACKAGE_PATH, "ProjectionDispatcherProvider.java").toString()), PROJECTION_DISPATCHER_PROVIDER_CONTENT);
    }

    private static final String HOME_DIRECTORY = OperatingSystem.detect().isWindows() ? "D:\\projects" : "/home";

    private static final String PROJECT_PATH = Paths.get(HOME_DIRECTORY, "xoom-app").toString();

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

    private static final String PROJECTION_DISPATCHER_PROVIDER_CONTENT =
            "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                    "public class ProjectionDispatcherProvider { \\n" +
                    "... \\n" +
                    "}";

}