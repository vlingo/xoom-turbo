// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.bootstrap;

import io.vlingo.xoom.OperatingSystem;
import io.vlingo.xoom.TextExpectation;
import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.*;
import io.vlingo.xoom.codegen.template.projections.ProjectionType;
import io.vlingo.xoom.codegen.template.storage.Queries;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import java.io.IOException;
import java.nio.file.Paths;

import static io.vlingo.xoom.codegen.language.Language.JAVA;
import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.*;

public class BootstrapGenerationStepTest {

    @Test
    public void testThatDefaultBootstrapIsGenerated() throws IOException {
        final CodeGenerationContext context =
                CodeGenerationContext.empty().with(PROJECTION_TYPE, ProjectionType.OPERATION_BASED.name());

        loadParameters(context, false);
        loadContents(context);

        new BootstrapGenerationStep().process(context);

        final Content bootstrap = context.findContent(BOOTSTRAP, "Bootstrap");

        Assert.assertEquals(7, context.contents().size());
        Assert.assertTrue(bootstrap.contains(TextExpectation.onJava().read("default-bootstrap")));
    }

    @Test
    public void testThatAnnotatedBootstrapIsGenerated() throws IOException {
        final CodeGenerationContext context =
                CodeGenerationContext.empty().with(PROJECTION_TYPE, ProjectionType.OPERATION_BASED.name());

        loadParameters(context, true);
        loadContents(context);

        new BootstrapGenerationStep().process(context);

        final Content bootstrap = context.findContent(BOOTSTRAP, "Bootstrap");

        Assert.assertEquals(7, context.contents().size());
        Assert.assertTrue(bootstrap.contains(TextExpectation.onJava().read("annotated-bootstrap")));
    }

    @Test
    public void testThatXoomInitializerIsGenerated() throws IOException {
        final CodeGenerationContext context =
                CodeGenerationContext.using(Mockito.mock(Filer.class), Mockito.mock(Element.class))
                        .with(PROJECTION_TYPE, ProjectionType.NONE.name())
                        .with(XOOM_INITIALIZER_NAME, "AnnotatedBootstrap");

        loadParameters(context, false);
        loadContents(context);

        context.registerTemplateProcessing(JAVA, loadAutoDispatchResourceHanlderTemplateData(), ORDER_RESOURCE_HANDLER_CONTENT);

        new BootstrapGenerationStep().process(context);

        final Content xoomInitializer = context.findContent(XOOM_INITIALIZER, "XoomInitializer");

        Assert.assertEquals(8, context.contents().size());
        Assert.assertTrue(xoomInitializer.contains(TextExpectation.onJava().read("xoom-initializer")));
    }

    private void loadParameters(final CodeGenerationContext context, final Boolean useAnnotation) {
        context.with(PACKAGE, "io.vlingo.xoomapp").with(APPLICATION_NAME, "xoom-app")
                .with(TARGET_FOLDER, HOME_DIRECTORY).with(STORAGE_TYPE, "STATE_STORE")
                .with(CQRS, "true").with(BLOCKING_MESSAGING, Boolean.TRUE.toString())
                .with(USE_ANNOTATIONS, useAnnotation.toString());
    }

    private void loadContents(final CodeGenerationContext context) {
        context.addContent(REST_RESOURCE, new OutputFile(RESOURCE_PACKAGE_PATH, "AuthorResource.java"), AUTHOR_RESOURCE_CONTENT);
        context.addContent(REST_RESOURCE, new OutputFile(RESOURCE_PACKAGE_PATH, "BookResource.java"), BOOK_RESOURCE_CONTENT);
        context.addContent(STORE_PROVIDER, new OutputFile(PERSISTENCE_PACKAGE_PATH, "CommandModelStateStoreProvider.java"), COMMAND_MODEL_STORE_PROVIDER_CONTENT);
        context.addContent(STORE_PROVIDER, new OutputFile(PERSISTENCE_PACKAGE_PATH, "QueryModelStateStoreProvider.java"), QUERY_MODEL_STORE_PROVIDER_CONTENT);
        context.addContent(EXCHANGE_BOOTSTRAP, new OutputFile(EXCHANGE_PACKAGE_PATH, "ExchangeBootstrap.java"), EXCHANGE_BOOTSTRAP_CONTENT);
        context.addContent(PROJECTION_DISPATCHER_PROVIDER, new OutputFile(PERSISTENCE_PACKAGE_PATH, "ProjectionDispatcherProvider.java"), PROJECTION_DISPATCHER_PROVIDER_CONTENT);
    }

    private TemplateData loadAutoDispatchResourceHanlderTemplateData() {
        final CodeGenerationParameter autoDispatchParameter =
                CodeGenerationParameter.of(AUTO_DISPATCH_NAME, "OrderResourceHandler")
                        .relate(QUERIES_PROTOCOL, "io.vlingo.xoomapp.infrastructure.persistence.OrderQueries")
                        .relate(Label.QUERIES_ACTOR, "io.vlingo.xoomapp.infrastructure.persistence.OrderQueriesActor");

        return new TemplateData() {
            @Override
            public TemplateParameters parameters() {
                return TemplateParameters.with(TemplateParameter.REST_RESOURCE_NAME, "OrderResourceHandler")
                        .and(TemplateParameter.PACKAGE_NAME, "io.vlingo.xoomapp.resource")
                        .and(TemplateParameter.QUERIES, Queries.from(autoDispatchParameter));
            }

            @Override
            public TemplateStandard standard() {
                return AUTO_DISPATCH_RESOURCE_HANDLER;
            }

            @Override
            public String filename() {
                return standard().resolveFilename("OrderResource", parameters());
            }
        };
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

    private static final String EXCHANGE_PACKAGE_PATH =
            Paths.get(INFRASTRUCTURE_PACKAGE_PATH, "exchange").toString();

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

    private static final String ORDER_RESOURCE_HANDLER_CONTENT =
            "package io.vlingo.xoomapp.resource; \\n" +
                    "public class OrderResourceHandler { \\n" +
                    "... \\n" +
                    "}";

    private static final String EXCHANGE_BOOTSTRAP_CONTENT =
            "package io.vlingo.xoomapp.infrastructure.exchange; \\n" +
                    "public class ExchangeBootstrap implements ExchangeInitializer { \\n" +
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