// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.codegen.autodispatch;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.TextExpectation;
import io.vlingo.xoom.codegen.content.CodeElementFormatter;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.dialect.ReservedWordsHandler;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameters;
import io.vlingo.xoom.codegen.template.OutputFile;
import io.vlingo.xoom.turbo.ComponentRegistry;
import io.vlingo.xoom.turbo.OperatingSystem;
import io.vlingo.xoom.turbo.annotation.codegen.AnnotationBasedTemplateStandard;
import io.vlingo.xoom.turbo.annotation.codegen.Label;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

import static io.vlingo.xoom.codegen.dialect.Dialect.JAVA;

public class AutoDispatchResourceHandlerGenerationStepTest {

  @Test
  public void testThatAutoDispatchResourceHandlersAreGenerated() {
    final CodeGenerationContext context =
            CodeGenerationContext.with(loadParameters())
                    .addContent(AnnotationBasedTemplateStandard.STORE_PROVIDER,
                            new OutputFile(PERSISTENCE_PACKAGE_PATH, "QueryModelStateStoreProvider.java"),
                            QUERY_MODEL_STORE_PROVIDER_CONTENT);

    new AutoDispatchResourceHandlerGenerationStep().process(context);

    Assert.assertEquals(3, context.contents().size());

    final Content authorResourceHandler =
            context.findContent(AnnotationBasedTemplateStandard.AUTO_DISPATCH_RESOURCE_HANDLER, "AuthorResourceHandler");

    Assert.assertTrue(authorResourceHandler.contains(TextExpectation.onJava().read("author-dispatch-resource-handler")));

    final Content bookResourceHandler =
            context.findContent(AnnotationBasedTemplateStandard.AUTO_DISPATCH_RESOURCE_HANDLER, "BookResourceHandler");

    Assert.assertTrue(bookResourceHandler.contains(TextExpectation.onJava().read("book-dispatch-resource-handler")));
  }

  private CodeGenerationParameters loadParameters() {
    final CodeGenerationParameter useAutoDispatch =
            CodeGenerationParameter.of(Label.USE_AUTO_DISPATCH, true);

    final CodeGenerationParameter cqrs =
            CodeGenerationParameter.of(Label.CQRS, true);

    final CodeGenerationParameter firstAuthorRouteParameter =
            CodeGenerationParameter.of(Label.ROUTE_SIGNATURE, "changeAuthorName(final String id, final AuthorData authorData)")
                    .relate(Label.ROUTE_HANDLER_INVOCATION, "changeAuthorNameHandler.handler.handle(author,authorData)")
                    .relate(Label.USE_CUSTOM_ROUTE_HANDLER_PARAM, "true")
                    .relate(Label.ROUTE_PATH, "/{id}/name")
                    .relate(Label.ROUTE_METHOD, "PATCH")
                    .relate(Label.INTERNAL_ROUTE_HANDLER, "false")
                    .relate(Label.ID, "authorId")
                    .relate(Label.ID_TYPE, "java.lang.String")
                    .relate(Label.BODY, "authorData")
                    .relate(Label.BODY_TYPE, "io.vlingo.xoomapp.infrastructure.AuthorData")
                    .relate(Label.ADAPTER_HANDLER_INVOCATION, "adaptStateHandler.handler.handle")
                    .relate(Label.USE_CUSTOM_ADAPTER_HANDLER_PARAM, "false");

    final CodeGenerationParameter secondAuthorRouteParameter =
            CodeGenerationParameter.of(Label.ROUTE_SIGNATURE, "queryById(final String id)")
                    .relate(Label.ROUTE_HANDLER_INVOCATION, "queryByIdHandler.handler.handle(id, authorQueries)")
                    .relate(Label.USE_CUSTOM_ROUTE_HANDLER_PARAM, "true")
                    .relate(Label.ROUTE_PATH, "/{id}")
                    .relate(Label.ROUTE_METHOD, "GET")
                    .relate(Label.INTERNAL_ROUTE_HANDLER, "false")
                    .relate(Label.ID, "authorId")
                    .relate(Label.ID_TYPE, "java.lang.String");

    final CodeGenerationParameter authorResourceParameter =
            CodeGenerationParameter.of(Label.AUTO_DISPATCH_NAME, "io.vlingo.xoomapp.resources.AuthorResource")
                    .relate(Label.HANDLERS_CONFIG_NAME, "io.vlingo.xoomapp.resources.AuthorHandlers")
                    .relate(Label.URI_ROOT, "/authors")
                    .relate(Label.MODEL_PROTOCOL, "io.vlingo.xoomapp.model.Author")
                    .relate(Label.MODEL_ACTOR, "io.vlingo.xoomapp.model.AuthorEntity")
                    .relate(Label.MODEL_DATA, "io.vlingo.xoomapp.infrastructure.AuthorData")
                    .relate(Label.QUERIES_PROTOCOL, "io.vlingo.xoomapp.infrastructure.persistence.AuthorQueries")
                    .relate(Label.QUERIES_ACTOR, "io.vlingo.xoomapp.infrastructure.persistence.AuthorQueriesActor")
                    .relate(firstAuthorRouteParameter).relate(secondAuthorRouteParameter);

    final CodeGenerationParameter bookRouteParameter =
            CodeGenerationParameter.of(Label.ROUTE_SIGNATURE, "queryBooks()")
                    .relate(Label.ROUTE_HANDLER_INVOCATION, "queryAllHandler.handler.handle")
                    .relate(Label.USE_CUSTOM_ROUTE_HANDLER_PARAM, "false")
                    .relate(Label.ROUTE_PATH, "")
                    .relate(Label.ROUTE_METHOD, "GET")
                    .relate(Label.INTERNAL_ROUTE_HANDLER, "false");

    final CodeGenerationParameter bookResourceParameter =
            CodeGenerationParameter.of(Label.AUTO_DISPATCH_NAME, "io.vlingo.xoomapp.resources.BookResource")
                    .relate(Label.URI_ROOT, "/books").relate(bookRouteParameter)
                    .relate(Label.HANDLERS_CONFIG_NAME, "io.vlingo.xoomapp.resources.BookHandlers")
                    .relate(Label.QUERIES_PROTOCOL, "io.vlingo.xoomapp.infrastructure.persistence.BookQueries")
                    .relate(Label.QUERIES_ACTOR, "io.vlingo.xoomapp.infrastructure.persistence.BookQueriesActor");

    return CodeGenerationParameters.from(authorResourceParameter, bookResourceParameter, useAutoDispatch, cqrs);
  }

  @Before
  public void registerFormatter() {
    ComponentRegistry.register(CodeElementFormatter.class, CodeElementFormatter.with(JAVA, ReservedWordsHandler.usingSuffix("_")));
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
