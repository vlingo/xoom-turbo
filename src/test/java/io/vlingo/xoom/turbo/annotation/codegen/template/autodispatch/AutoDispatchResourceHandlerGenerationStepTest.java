// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.codegen.template.autodispatch;

import io.vlingo.xoom.turbo.OperatingSystem;
import io.vlingo.xoom.turbo.codegen.CodeGenerationContext;
import io.vlingo.xoom.turbo.codegen.TextExpectation;
import io.vlingo.xoom.turbo.codegen.content.Content;
import io.vlingo.xoom.turbo.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.turbo.codegen.parameter.CodeGenerationParameters;
import io.vlingo.xoom.turbo.codegen.template.OutputFile;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static io.vlingo.xoom.turbo.annotation.codegen.template.AnnotationBasedTemplateStandard.AUTO_DISPATCH_RESOURCE_HANDLER;
import static io.vlingo.xoom.turbo.annotation.codegen.template.AnnotationBasedTemplateStandard.STORE_PROVIDER;
import static io.vlingo.xoom.turbo.annotation.codegen.template.Label.*;

public class AutoDispatchResourceHandlerGenerationStepTest {

    @Test
    public void testThatAutoDispatchResourceHandlersAreGenerated() throws IOException {
        final CodeGenerationContext context =
                CodeGenerationContext.with(loadParameters())
                        .addContent(STORE_PROVIDER,
                                new OutputFile(PERSISTENCE_PACKAGE_PATH, "QueryModelStateStoreProvider.java"),
                                QUERY_MODEL_STORE_PROVIDER_CONTENT);

        new AutoDispatchResourceHandlerGenerationStep().process(context);

        Assert.assertEquals(3, context.contents().size());

        final Content authorResourceHandler =
                context.findContent(AUTO_DISPATCH_RESOURCE_HANDLER, "AuthorResourceHandler");

        Assert.assertTrue(authorResourceHandler.contains(TextExpectation.onJava().read("author-dispatch-resource-handler")));

        final Content bookResourceHandler =
                context.findContent(AUTO_DISPATCH_RESOURCE_HANDLER, "BookResourceHandler");

        Assert.assertTrue(bookResourceHandler.contains(TextExpectation.onJava().read("book-dispatch-resource-handler")));
    }

    private CodeGenerationParameters loadParameters() {
        final CodeGenerationParameter useAutoDispatch =
                CodeGenerationParameter.of(USE_AUTO_DISPATCH, true);

        final CodeGenerationParameter cqrs =
                CodeGenerationParameter.of(CQRS, true);

        final CodeGenerationParameter firstAuthorRouteParameter =
                CodeGenerationParameter.of(ROUTE_SIGNATURE, "changeAuthorName(final String id, final AuthorData authorData)")
                        .relate(ROUTE_HANDLER_INVOCATION, "changeAuthorNameHandler.handler.handle(author,authorData)")
                        .relate(USE_CUSTOM_ROUTE_HANDLER_PARAM, "true")
                        .relate(ROUTE_PATH, "/{id}/name")
                        .relate(ROUTE_METHOD, "PATCH")
                        .relate(INTERNAL_ROUTE_HANDLER, "false")
                        .relate(ID, "authorId")
                        .relate(ID_TYPE, "java.lang.String")
                        .relate(BODY, "authorData")
                        .relate(BODY_TYPE, "io.vlingo.xoomapp.infrastructure.AuthorData")
                        .relate(ADAPTER_HANDLER_INVOCATION, "adaptStateHandler.handler.handle")
                        .relate(USE_CUSTOM_ADAPTER_HANDLER_PARAM, "false");

        final CodeGenerationParameter secondAuthorRouteParameter =
                CodeGenerationParameter.of(ROUTE_SIGNATURE, "queryById(final String id)")
                        .relate(ROUTE_HANDLER_INVOCATION, "queryByIdHandler.handler.handle(id, authorQueries)")
                        .relate(USE_CUSTOM_ROUTE_HANDLER_PARAM, "true")
                        .relate(ROUTE_PATH, "/{id}")
                        .relate(ROUTE_METHOD, "GET")
                        .relate(INTERNAL_ROUTE_HANDLER, "false")
                        .relate(ID, "authorId")
                        .relate(ID_TYPE, "java.lang.String");

        final CodeGenerationParameter authorResourceParameter =
                CodeGenerationParameter.of(AUTO_DISPATCH_NAME, "io.vlingo.xoomapp.resources.AuthorResource")
                        .relate(HANDLERS_CONFIG_NAME, "io.vlingo.xoomapp.resources.AuthorHandlers")
                        .relate(URI_ROOT, "/authors")
                        .relate(MODEL_PROTOCOL, "io.vlingo.xoomapp.model.Author")
                        .relate(MODEL_ACTOR, "io.vlingo.xoomapp.model.AuthorEntity")
                        .relate(MODEL_DATA, "io.vlingo.xoomapp.infrastructure.AuthorData")
                        .relate(QUERIES_PROTOCOL, "io.vlingo.xoomapp.infrastructure.persistence.AuthorQueries")
                        .relate(QUERIES_ACTOR, "io.vlingo.xoomapp.infrastructure.persistence.AuthorQueriesActor")
                        .relate(firstAuthorRouteParameter).relate(secondAuthorRouteParameter);

        final CodeGenerationParameter bookRouteParameter =
                CodeGenerationParameter.of(ROUTE_SIGNATURE, "queryBooks()")
                        .relate(ROUTE_HANDLER_INVOCATION, "queryAllHandler.handler.handle")
                        .relate(USE_CUSTOM_ROUTE_HANDLER_PARAM, "false")
                        .relate(ROUTE_PATH, "")
                        .relate(ROUTE_METHOD, "GET")
                        .relate(INTERNAL_ROUTE_HANDLER, "false");

        final CodeGenerationParameter bookResourceParameter =
                CodeGenerationParameter.of(AUTO_DISPATCH_NAME, "io.vlingo.xoomapp.resources.BookResource")
                        .relate(URI_ROOT, "/books").relate(bookRouteParameter)
                        .relate(HANDLERS_CONFIG_NAME, "io.vlingo.xoomapp.resources.BookHandlers")
                        .relate(QUERIES_PROTOCOL, "io.vlingo.xoomapp.infrastructure.persistence.BookQueries")
                        .relate(QUERIES_ACTOR, "io.vlingo.xoomapp.infrastructure.persistence.BookQueriesActor");

        return CodeGenerationParameters.from(authorResourceParameter, bookResourceParameter, useAutoDispatch, cqrs);
    }

    private static final String PROJECT_PATH =
            OperatingSystem.detect().isWindows() ?
                    Paths.get("D:\\projects", "xoom-app").toString() :
                    Paths.get("/home", "xoom-app").toString();

    private static final String INFRASTRUCTURE_PACKAGE_PATH =
            Paths.get(PROJECT_PATH, "src", "main", "java",
                    "io", "vlingo", "xoomapp", "infrastructure").toString();

    private static final String PERSISTENCE_PACKAGE_PATH =
            Paths.get(INFRASTRUCTURE_PACKAGE_PATH, "persistence").toString();

    private static final String QUERY_MODEL_STORE_PROVIDER_CONTENT =
            "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                    "public class QueryModelStateStoreProvider { \\n" +
                    "... \\n" +
                    "}";
}
