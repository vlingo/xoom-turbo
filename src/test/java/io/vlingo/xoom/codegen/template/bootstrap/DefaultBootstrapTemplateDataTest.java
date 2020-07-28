// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.bootstrap;

import io.vlingo.xoom.OperatingSystem;
import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.CodeGenerationParameter;
import io.vlingo.xoom.codegen.file.ImportParameter;
import io.vlingo.xoom.codegen.template.TemplateFile;
import io.vlingo.xoom.codegen.template.TemplateParameter;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.projections.ProjectionType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.vlingo.xoom.codegen.CodeGenerationParameter.APPLICATION_NAME;
import static io.vlingo.xoom.codegen.CodeGenerationParameter.BLOCKING_MESSAGING;
import static io.vlingo.xoom.codegen.CodeGenerationParameter.STORAGE_TYPE;
import static io.vlingo.xoom.codegen.CodeGenerationParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateParameter.REST_RESOURCES;
import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.*;
import static io.vlingo.xoom.codegen.template.storage.StorageType.STATE_STORE;

public class DefaultBootstrapTemplateDataTest {

    @Test
    public void testBootstrapTemplateDataGenerationWithCQRSAndProjections() {
        final Map<CodeGenerationParameter, String> codeGenerationParameters =
                new HashMap<CodeGenerationParameter, String>() {{
                    put(PACKAGE, "io.vlingo.xoomapp");
                    put(APPLICATION_NAME, "xoom-app");
                    put(STORAGE_TYPE, STATE_STORE.name());
                    put(CQRS, Boolean.TRUE.toString());
                    put(PROJECTIONS, ProjectionType.EVENT_BASED.name());
                    put(ANNOTATIONS, Boolean.FALSE.toString());
                    put(BLOCKING_MESSAGING, Boolean.FALSE.toString());
                }};

        final CodeGenerationContext context =
                CodeGenerationContext.with(codeGenerationParameters)
                        .addContent(REST_RESOURCE, new TemplateFile(RESOURCE_PACKAGE_PATH, "AuthorResource.java"), AUTHOR_RESOURCE_CONTENT)
                        .addContent(REST_RESOURCE, new TemplateFile(RESOURCE_PACKAGE_PATH, "BookResource.java"), BOOK_RESOURCE_CONTENT)
                        .addContent(STORAGE_PROVIDER, new TemplateFile(PERSISTENCE_PACKAGE_PATH, "CommandModelStateStoreProvider.java"), COMMAND_MODEL_STORE_PROVIDER_CONTENT)
                        .addContent(STORAGE_PROVIDER, new TemplateFile(PERSISTENCE_PACKAGE_PATH, "QueryModelStateStoreProvider.java"), QUERY_MODEL_STORE_PROVIDER_CONTENT)
                        .addContent(PROJECTION_DISPATCHER_PROVIDER, new TemplateFile(PERSISTENCE_PACKAGE_PATH, "ProjectionDispatcherProvider.java"), PROJECTION_DISPATCHER_PROVIDER_CONTENT);

        final TemplateParameters parameters =
                BootstrapTemplateData.from(context).parameters();

        Assert.assertEquals(EXPECTED_PACKAGE, parameters.find(PACKAGE_NAME));
        Assert.assertEquals(6, parameters.<List>find(IMPORTS).size());
        Assert.assertEquals("io.vlingo.xoomapp.infrastructure.persistence.CommandModelStateStoreProvider", parameters.<List<ImportParameter>>find(IMPORTS).get(0).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.xoomapp.infrastructure.persistence.QueryModelStateStoreProvider", parameters.<List<ImportParameter>>find(IMPORTS).get(1).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.xoomapp.infrastructure.persistence.ProjectionDispatcherProvider", parameters.<List<ImportParameter>>find(IMPORTS).get(2).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.lattice.model.stateful.StatefulTypeRegistry", parameters.<List<ImportParameter>>find(IMPORTS).get(3).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.xoomapp.resource.AuthorResource", parameters.<List<ImportParameter>>find(IMPORTS).get(4).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.xoomapp.resource.BookResource", parameters.<List<ImportParameter>>find(IMPORTS).get(5).getQualifiedClassName());

        Assert.assertEquals(2, parameters.<List>find(REST_RESOURCES).size());
        Assert.assertEquals("AuthorResource", parameters.<List<RestResourcesParameter>>find(REST_RESOURCES).get(0).getClassName());
        Assert.assertEquals("authorResource", parameters.<List<RestResourcesParameter>>find(REST_RESOURCES).get(0).getObjectName());
        Assert.assertEquals(false, parameters.<List<RestResourcesParameter>>find(REST_RESOURCES).get(0).isLast());
        Assert.assertEquals("BookResource", parameters.<List<RestResourcesParameter>>find(REST_RESOURCES).get(1).getClassName());
        Assert.assertEquals("bookResource", parameters.<List<RestResourcesParameter>>find(REST_RESOURCES).get(1).getObjectName());
        Assert.assertEquals(true, parameters.<List<RestResourcesParameter>>find(REST_RESOURCES).get(1).isLast());

        Assert.assertEquals(3, parameters.<List>find(PROVIDERS).size());
        Assert.assertEquals("QueryModelStateStoreProvider", parameters.<List<ProviderParameter>>find(PROVIDERS).get(0).getInitialization());
        Assert.assertEquals("stage, statefulTypeRegistry", parameters.<List<ProviderParameter>>find(PROVIDERS).get(0).getArguments());
        Assert.assertEquals("final ProjectionDispatcherProvider projectionDispatcherProvider = ProjectionDispatcherProvider", parameters.<List<ProviderParameter>>find(PROVIDERS).get(1).getInitialization());
        Assert.assertEquals("stage", parameters.<List<ProviderParameter>>find(PROVIDERS).get(1).getArguments());
        Assert.assertEquals("CommandModelStateStoreProvider", parameters.<List<ProviderParameter>>find(PROVIDERS).get(2).getInitialization());
        Assert.assertEquals("stage, statefulTypeRegistry, projectionDispatcherProvider.storeDispatcher", parameters.<List<ProviderParameter>>find(PROVIDERS).get(2).getArguments());

        Assert.assertEquals(1, parameters.<List>find(TYPE_REGISTRIES).size());
        Assert.assertEquals("StatefulTypeRegistry", parameters.<List<TypeRegistryParameter>>find(TYPE_REGISTRIES).get(0).getClassName());
        Assert.assertEquals("statefulTypeRegistry", parameters.<List<TypeRegistryParameter>>find(TYPE_REGISTRIES).get(0).getObjectName());

        Assert.assertEquals(true, parameters.find(USE_PROJECTIONS));
        Assert.assertEquals("xoom-app", parameters.find(TemplateParameter.APPLICATION_NAME));
    }

    @Test
    public void testBootstrapTemplateDataGenerationWithoutCQRSAndProjections() {
        final Map<CodeGenerationParameter, String> codeGenerationParameters =
                new HashMap<CodeGenerationParameter, String>() {{
                    put(PACKAGE, "io.vlingo.xoomapp");
                    put(APPLICATION_NAME, "xoom-app");
                    put(STORAGE_TYPE, STATE_STORE.name());
                    put(CQRS, Boolean.FALSE.toString());
                    put(PROJECTIONS, ProjectionType.NONE.name());
                    put(ANNOTATIONS, Boolean.FALSE.toString());
                    put(BLOCKING_MESSAGING, Boolean.FALSE.toString());
                }};

        final CodeGenerationContext context =
                CodeGenerationContext.with(codeGenerationParameters)
                        .addContent(REST_RESOURCE, new TemplateFile(RESOURCE_PACKAGE_PATH, "AuthorResource.java"), AUTHOR_RESOURCE_CONTENT)
                        .addContent(STORAGE_PROVIDER, new TemplateFile(PERSISTENCE_PACKAGE_PATH, "StateStoreProvider.java"), SINGLE_MODEL_STORE_PROVIDER_CONTENT);

        final TemplateParameters parameters =
                BootstrapTemplateData.from(context).parameters();

        Assert.assertEquals(EXPECTED_PACKAGE, parameters.find(PACKAGE_NAME));
        Assert.assertEquals(3, parameters.<List>find(IMPORTS).size());
        Assert.assertEquals("io.vlingo.xoomapp.infrastructure.persistence.StateStoreProvider", parameters.<List<ImportParameter>>find(IMPORTS).get(0).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.lattice.model.stateful.StatefulTypeRegistry", parameters.<List<ImportParameter>>find(IMPORTS).get(1).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.xoomapp.resource.AuthorResource", parameters.<List<ImportParameter>>find(IMPORTS).get(2).getQualifiedClassName());

        Assert.assertEquals(1, parameters.<List>find(REST_RESOURCES).size());
        Assert.assertEquals("AuthorResource", parameters.<List<RestResourcesParameter>>find(REST_RESOURCES).get(0).getClassName());
        Assert.assertEquals("authorResource", parameters.<List<RestResourcesParameter>>find(REST_RESOURCES).get(0).getObjectName());
        Assert.assertEquals(true, parameters.<List<RestResourcesParameter>>find(REST_RESOURCES).get(0).isLast());

        Assert.assertEquals(1, parameters.<List>find(PROVIDERS).size());
        Assert.assertEquals("StateStoreProvider", parameters.<List<ProviderParameter>>find(PROVIDERS).get(0).getInitialization());
        Assert.assertEquals("stage, statefulTypeRegistry", parameters.<List<ProviderParameter>>find(PROVIDERS).get(0).getArguments());

        Assert.assertEquals(1, parameters.<List>find(TYPE_REGISTRIES).size());
        Assert.assertEquals("StatefulTypeRegistry", parameters.<List<TypeRegistryParameter>>find(TYPE_REGISTRIES).get(0).getClassName());
        Assert.assertEquals("statefulTypeRegistry", parameters.<List<TypeRegistryParameter>>find(TYPE_REGISTRIES).get(0).getObjectName());

        Assert.assertEquals(false, parameters.find(USE_PROJECTIONS));
        Assert.assertEquals("xoom-app", parameters.find(TemplateParameter.APPLICATION_NAME));
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

