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
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateFile;
import io.vlingo.xoom.codegen.template.storage.QueriesTemplateDataFactory;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Arrays;

import static io.vlingo.xoom.codegen.parameter.Label.ROUTE_METHOD;
import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateParameter.PACKAGE_NAME;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE;
import static io.vlingo.xoom.codegen.template.TemplateStandard.QUERIES_ACTOR;
import static io.vlingo.xoom.codegen.template.TemplateStandard.*;

public class AutoDispatchMappingGenerationStepTest {

    @Test
    public void testThatAutoDispatchMappingsAreGenerated() {
        final String basePackage = "io.vlingo.xoomapp";
        final String persistencePackage = basePackage + ".infrastructure.persistence";

        final CodeGenerationParameters parameters =
                CodeGenerationParameters.from(CodeGenerationParameter.of(PACKAGE, basePackage),
                        CodeGenerationParameter.of(CQRS, true), authorAggregate());

        final CodeGenerationContext context =
                CodeGenerationContext.with(parameters).contents(contents());

        QueriesTemplateDataFactory.from(persistencePackage, true, Arrays.asList(contents())).stream()
                .forEach(data -> {
                    final String packageName = data.parameters().find(PACKAGE_NAME);
                    context.registerTemplateProcessing(data, "package " + packageName + ";");
                });

        new AutoDispatchMappingGenerationStep().process(context);

        Assert.assertEquals(18, context.contents().size());

        final Content authorMappingContent =
                context.contents().stream().filter(content -> content.retrieveClassName().equals("AuthorResource"))
                        .findFirst().get();

        Assert.assertTrue(authorMappingContent.contains("@AutoDispatch(path=\"/authors\", handlers=AuthorResourceHandlers.class)"));
        Assert.assertTrue(authorMappingContent.contains("@Queries(protocol = AuthorQueries.class, actor = AuthorQueriesActor.class)"));
        Assert.assertTrue(authorMappingContent.contains("@Route(method = POST, handler = AuthorResourceHandlers.WITH_NAME)"));
        Assert.assertTrue(authorMappingContent.contains("@ResponseAdapter(handler = AuthorResourceHandlers.ADAPT_STATE)"));
        Assert.assertTrue(authorMappingContent.contains("Completes<Response> withName(@Body final AuthorData data);"));
        Assert.assertTrue(authorMappingContent.contains("@Route(method = PATCH, path = \"/{id}/rank\", handler = AuthorResourceHandlers.CHANGE_RANK)"));
        Assert.assertTrue(authorMappingContent.contains("Completes<Response> changeRank(@Id final long id, @Body final AuthorData data);"));
        Assert.assertTrue(authorMappingContent.contains("@Route(method = GET, handler = AuthorResourceHandlers.AUTHORS)"));
        Assert.assertTrue(authorMappingContent.contains("Completes<Response> authors();"));

        final Content authorHandlersMappingContent =
                context.contents().stream().filter(content -> content.retrieveClassName().equals("AuthorResourceHandlers"))
                        .findFirst().get();

        Assert.assertTrue(authorHandlersMappingContent.contains("public static final int WITH_NAME = 0;"));
        Assert.assertTrue(authorHandlersMappingContent.contains("public static final int CHANGE_RANK = 1;"));
        Assert.assertTrue(authorHandlersMappingContent.contains("public static final int AUTHORS = 2;"));
        Assert.assertTrue(authorHandlersMappingContent.contains("public static final HandlerEntry<Three<Completes<AuthorState>, Stage, AuthorData>> WITH_NAME_HANDLER ="));
        Assert.assertTrue(authorHandlersMappingContent.contains("HandlerEntry.of(WITH_NAME, ($stage, data) -> Author.withName($stage, data.name));"));
        Assert.assertTrue(authorHandlersMappingContent.contains("public static final HandlerEntry<Three<Completes<AuthorState>, Author, AuthorData>> CHANGE_RANK_HANDLER ="));
        Assert.assertTrue(authorHandlersMappingContent.contains("HandlerEntry.of(CHANGE_RANK, (author, data) -> author.changeRank(data.rank));"));
        Assert.assertTrue(authorHandlersMappingContent.contains("public static final HandlerEntry<Two<AuthorData, AuthorState>> ADAPT_STATE_HANDLER ="));
        Assert.assertTrue(authorHandlersMappingContent.contains("HandlerEntry.of(ADAPT_STATE, AuthorData::from);"));
        Assert.assertTrue(authorHandlersMappingContent.contains("public static final HandlerEntry<Two<Completes<Collection<AuthorData>>, AuthorQueries>> QUERY_ALL_HANDLER ="));
        Assert.assertTrue(authorHandlersMappingContent.contains("HandlerEntry.of(AUTHORS, AuthorQueries::authors);"));
    }

    private Content[] contents() {
        return new Content[]{
                Content.with(AGGREGATE_PROTOCOL, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "Author.java"), null, null, AUTHOR_CONTENT_TEXT),
                Content.with(AGGREGATE_PROTOCOL, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "Book.java"), null, null, BOOK_CONTENT_TEXT),
                Content.with(AGGREGATE, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "AuthorEntity.java"), null, null, AUTHOR_AGGREGATE_CONTENT_TEXT),
                Content.with(AGGREGATE, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookEntity.java"), null, null, BOOK_AGGREGATE_CONTENT_TEXT),
                Content.with(AGGREGATE_STATE, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "AuthorState.java"), null, null, AUTHOR_STATE_CONTENT_TEXT),
                Content.with(AGGREGATE_STATE, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookState.java"), null, null, BOOK_STATE_CONTENT_TEXT),
                Content.with(DATA_OBJECT, new TemplateFile(Paths.get(INFRASTRUCTURE_PACKAGE_PATH).toString(), "AuthorData.java"), null, null, AUTHOR_DATA_CONTENT_TEXT),
                Content.with(DATA_OBJECT, new TemplateFile(Paths.get(INFRASTRUCTURE_PACKAGE_PATH).toString(), "BookData.java"), null, null, BOOK_DATA_CONTENT_TEXT),
                Content.with(QUERIES, new TemplateFile(Paths.get(PERSISTENCE_PACKAGE_PATH).toString(), "AuthorQueries.java"), null, null, AUTHOR_QUERIES_CONTENT_TEXT),
                Content.with(QUERIES, new TemplateFile(Paths.get(PERSISTENCE_PACKAGE_PATH).toString(), "BookQueries.java"), null, null, BOOK_QUERIES_CONTENT_TEXT),
                Content.with(QUERIES_ACTOR, new TemplateFile(Paths.get(PERSISTENCE_PACKAGE_PATH).toString(), "AuthorQueriesActor.java"), null, null, AUTHOR_QUERIES_ACTOR_CONTENT_TEXT),
                Content.with(QUERIES_ACTOR, new TemplateFile(Paths.get(PERSISTENCE_PACKAGE_PATH).toString(), "BookQueriesActor.java"), null, null, BOOK_QUERIES_ACTOR_CONTENT_TEXT)
        };
    }

    private CodeGenerationParameter authorAggregate() {
        final CodeGenerationParameter idField =
                CodeGenerationParameter.of(Label.STATE_FIELD, "id")
                        .relate(Label.FIELD_TYPE, "long");

        final CodeGenerationParameter nameField =
                CodeGenerationParameter.of(Label.STATE_FIELD, "name")
                        .relate(Label.FIELD_TYPE, "String");

        final CodeGenerationParameter rankField =
                CodeGenerationParameter.of(Label.STATE_FIELD, "rank")
                        .relate(Label.FIELD_TYPE, "int");

        final CodeGenerationParameter authorRegisteredEvent =
                CodeGenerationParameter.of(Label.DOMAIN_EVENT, "AuthorRegistered")
                        .relate(idField).relate(nameField);

        final CodeGenerationParameter authorRankedEvent =
                CodeGenerationParameter.of(Label.DOMAIN_EVENT, "AuthorRanked")
                        .relate(idField).relate(rankField);

        final CodeGenerationParameter factoryMethod =
                CodeGenerationParameter.of(Label.AGGREGATE_METHOD, "withName")
                        .relate(Label.METHOD_PARAMETER, "name")
                        .relate(FACTORY_METHOD, "true")
                        .relate(authorRegisteredEvent);

        final CodeGenerationParameter rankMethod =
                CodeGenerationParameter.of(Label.AGGREGATE_METHOD, "changeRank")
                        .relate(Label.METHOD_PARAMETER, "rank")
                        .relate(authorRankedEvent);

        final CodeGenerationParameter withNameRoute =
                CodeGenerationParameter.of(ROUTE_SIGNATURE, "withName")
                        .relate(ROUTE_METHOD, "POST")
                        .relate(ROUTE_PATH, "/")
                        .relate(REQUIRE_ENTITY_LOADING, "false");

        final CodeGenerationParameter changeRankRoute =
                CodeGenerationParameter.of(ROUTE_SIGNATURE, "changeRank")
                        .relate(ROUTE_METHOD, "PATCH")
                        .relate(ROUTE_PATH, "/{id}/rank")
                        .relate(REQUIRE_ENTITY_LOADING, "true");

        return CodeGenerationParameter.of(Label.AGGREGATE, "Author")
                .relate(URI_ROOT, "/authors").relate(idField)
                .relate(nameField).relate(rankField).relate(factoryMethod)
                .relate(rankMethod).relate(withNameRoute).relate(changeRankRoute)
                .relate(authorRegisteredEvent).relate(authorRankedEvent);
    }

    private static final String PROJECT_PATH =
            OperatingSystem.detect().isWindows() ?
                    Paths.get("D:\\projects", "xoom-app").toString() :
                    Paths.get("/home", "xoom-app").toString();

    private static final String MODEL_PACKAGE_PATH =
            Paths.get(PROJECT_PATH, "src", "main", "java",
                    "io", "vlingo", "xoomapp", "model").toString();

    private static final String INFRASTRUCTURE_PACKAGE_PATH =
            Paths.get(PROJECT_PATH, "src", "main", "java",
                    "io", "vlingo", "xoomapp", "infrastructure").toString();

    private static final String PERSISTENCE_PACKAGE_PATH =
            Paths.get(INFRASTRUCTURE_PACKAGE_PATH, "persistence").toString();

    private static final String AUTHOR_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.author; \\n" +
                    "public interface Author { \\n" +
                    "... \\n" +
                    "}";

    private static final String BOOK_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.book; \\n" +
                    "public interface Book { \\n" +
                    "... \\n" +
                    "}";

    private static final String AUTHOR_STATE_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.author; \\n" +
                    "public class AuthorState { \\n" +
                    "... \\n" +
                    "}";

    private static final String BOOK_STATE_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.book; \\n" +
                    "public class BookState { \\n" +
                    "... \\n" +
                    "}";

    private static final String AUTHOR_AGGREGATE_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.author; \\n" +
                    "public class AuthorEntity { \\n" +
                    "... \\n" +
                    "}";

    private static final String BOOK_AGGREGATE_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.book; \\n" +
                    "public class BookEntity { \\n" +
                    "... \\n" +
                    "}";

    private static final String AUTHOR_DATA_CONTENT_TEXT =
            "package io.vlingo.xoomapp.infrastructure; \\n" +
                    "public class AuthorData { \\n" +
                    "... \\n" +
                    "}";

    private static final String BOOK_DATA_CONTENT_TEXT =
            "package io.vlingo.xoomapp.infrastructure; \\n" +
                    "public class BookData { \\n" +
                    "... \\n" +
                    "}";

    private static final String AUTHOR_QUERIES_CONTENT_TEXT =
            "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                    "public interface AuthorQueries { \\n" +
                    "... \\n" +
                    "}";

    private static final String BOOK_QUERIES_CONTENT_TEXT =
            "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                    "public interface BookQueries { \\n" +
                    "... \\n" +
                    "}";

    private static final String AUTHOR_QUERIES_ACTOR_CONTENT_TEXT =
            "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                    "public class AuthorQueriesActor { \\n" +
                    "... \\n" +
                    "}";

    private static final String BOOK_QUERIES_ACTOR_CONTENT_TEXT =
            "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                    "public class BookQueriesActor { \\n" +
                    "... \\n" +
                    "}";
}
