// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.autodispatch;

import io.vlingo.xoom.OperatingSystem;
import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameters;
import io.vlingo.xoom.codegen.template.TemplateFile;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;

import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.STORE_PROVIDER;

public class AutoDispatchResourceHandlerGenerationStepTest {

    @Test
    public void testThatAutoDispatchResourceHandlersAreGenerated() {
        final CodeGenerationContext context =
                CodeGenerationContext.with(loadParameters())
                        .addContent(STORE_PROVIDER,
                                new TemplateFile(PERSISTENCE_PACKAGE_PATH, "QueryModelStateStoreProvider.java"),
                                QUERY_MODEL_STORE_PROVIDER_CONTENT);

        new AutoDispatchResourceHandlerGenerationStep().process(context);

        Assert.assertEquals(3, context.contents().size());

        final Content authorResourceHandlerContent =
                context.contents().stream().filter(content ->
                        content.retrieveClassName().equals("AuthorResourceHandler")).findFirst().get();

        Assert.assertTrue(authorResourceHandlerContent.contains("import io.vlingo.xoomapp.infrastructure.persistence.QueryModelStateStoreProvider;"));
    }

    private CodeGenerationParameters loadParameters() {
        final CodeGenerationParameter useAutoDispatch =
                CodeGenerationParameter.of(USE_AUTO_DISPATCH, true);

        final CodeGenerationParameter firstAuthorRouteParameter =
                CodeGenerationParameter.of(ROUTE_SIGNATURE, "changeAuthorName(final String authorId, final AuthorData authorData)")
                        .relate(ROUTE_HANDLER, "changeName(authorData.name)")
                        .relate(ROUTE_PATH, "/authors/{authorId}/name")
                        .relate(ROUTE_METHOD, "PATCH")
                        .relate(CUSTOM_ROUTE, "false")
                        .relate(ID, "authorId")
                        .relate(ID_TYPE, "java.lang.String")
                        .relate(BODY, "authorData")
                        .relate(BODY_TYPE, "io.vlingo.xoomapp.infrastructure.AuthorData")
                        .relate(RESPONSE_DATA, "AuthorData.from");

        final CodeGenerationParameter secondAuthorRouteParameter =
                CodeGenerationParameter.of(ROUTE_SIGNATURE, "queryById(final String authorId)")
                        .relate(ROUTE_HANDLER, "authorOf(authorId)")
                        .relate(ROUTE_PATH, "/authors/{authorId}")
                        .relate(ROUTE_METHOD, "GET")
                        .relate(CUSTOM_ROUTE, "false")
                        .relate(ID, "authorId")
                        .relate(ID_TYPE, "java.lang.String");

        final CodeGenerationParameter authorResourceParameter =
                CodeGenerationParameter.of(AUTO_DISPATCH_NAME, "io.vlingo.xoomapp.resources.AuthorResource")
                        .relate(URI_ROOT, "/authors")
                        .relate(MODEL_PROTOCOL, "io.vlingo.xoomapp.model.Author")
                        .relate(MODEL_ACTOR, "io.vlingo.xoomapp.model.AuthorEntity")
                        .relate(MODEL_DATA, "io.vlingo.xoomapp.infrastructure.AuthorData")
                        .relate(QUERIES_PROTOCOL, "io.vlingo.xoomapp.infrastructure.persistence.AuthorQueries")
                        .relate(QUERIES_ACTOR, "io.vlingo.xoomapp.infrastructure.persistence.AuthorQueriesActor")
                        .relate(firstAuthorRouteParameter).relate(secondAuthorRouteParameter);

        final CodeGenerationParameter bookRouteParameter =
                CodeGenerationParameter.of(ROUTE_SIGNATURE, "queryBooks()")
                        .relate(ROUTE_HANDLER, "retrieveAll()")
                        .relate(ROUTE_PATH, "/books")
                        .relate(ROUTE_METHOD, "GET")
                        .relate(CUSTOM_ROUTE, "false");

        final CodeGenerationParameter bookResourceParameter =
                CodeGenerationParameter.of(AUTO_DISPATCH_NAME, "io.vlingo.xoomapp.resources.BookResource")
                        .relate(URI_ROOT, "/books").relate(bookRouteParameter)
                        .relate(QUERIES_PROTOCOL, "io.vlingo.xoomapp.infrastructure.persistence.BookQueries")
                        .relate(QUERIES_ACTOR, "io.vlingo.xoomapp.infrastructure.persistence.BookQueriesActor");

        return CodeGenerationParameters.from(authorResourceParameter, bookResourceParameter, useAutoDispatch);
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
