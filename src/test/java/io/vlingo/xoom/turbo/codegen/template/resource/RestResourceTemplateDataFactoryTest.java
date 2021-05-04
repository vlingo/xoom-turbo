// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.codegen.template.resource;

import io.vlingo.xoom.turbo.OperatingSystem;
import io.vlingo.xoom.turbo.codegen.content.Content;
import io.vlingo.xoom.turbo.codegen.designer.Label;
import io.vlingo.xoom.turbo.codegen.language.Language;
import io.vlingo.xoom.turbo.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.turbo.codegen.parameter.CodeGenerationParameters;
import io.vlingo.xoom.turbo.codegen.template.*;
import io.vlingo.xoom.turbo.codegen.template.storage.Queries;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static io.vlingo.xoom.turbo.codegen.designer.Label.*;
import static io.vlingo.xoom.turbo.codegen.designer.Label.ROUTE_METHOD;
import static io.vlingo.xoom.turbo.codegen.template.DesignerTemplateStandard.*;
import static io.vlingo.xoom.turbo.codegen.template.DesignerTemplateStandard.QUERIES_ACTOR;

public class RestResourceTemplateDataFactoryTest {

    @Test
    public void testThatRestResourceIsGeneratedWithCQRS() {
        final CodeGenerationParameter packageParameter =
                CodeGenerationParameter.of(PACKAGE, "io.vlingo.xoomapp");

        final CodeGenerationParameter useCQRSParameter =
                CodeGenerationParameter.of(CQRS, "true");

        final CodeGenerationParameter language =
                CodeGenerationParameter.of(LANGUAGE, Language.JAVA);

        final CodeGenerationParameters parameters =
                CodeGenerationParameters.from(packageParameter, language,
                        useCQRSParameter, authorAggregate());

        final List<TemplateData> templatesData =
                RestResourceTemplateDataFactory.build(parameters, contents());

        Assert.assertEquals(1, templatesData.size());

        final TemplateParameters templateParameters = templatesData.get(0).parameters();

        Assert.assertTrue(templateParameters.hasImport("io.vlingo.xoomapp.model.author.Author"));
        Assert.assertTrue(templateParameters.hasImport("io.vlingo.xoomapp.model.author.AuthorEntity"));
        Assert.assertTrue(templateParameters.hasImport("io.vlingo.xoomapp.infrastructure.AuthorData"));
        Assert.assertTrue(templateParameters.hasImport("io.vlingo.xoomapp.infrastructure.persistence.AuthorQueries"));
        Assert.assertEquals("io.vlingo.xoomapp.infrastructure.resource", templateParameters.find(TemplateParameter.PACKAGE_NAME));
        Assert.assertEquals("AuthorResource", templateParameters.find(TemplateParameter.REST_RESOURCE_NAME));
        Assert.assertEquals("Author", templateParameters.find(TemplateParameter.MODEL_PROTOCOL));
        Assert.assertEquals("AuthorEntity", templateParameters.find(TemplateParameter.MODEL_ACTOR));
        Assert.assertEquals("QueryModelStateStoreProvider", templateParameters.find(TemplateParameter.STORE_PROVIDER_NAME));
        Assert.assertEquals("/authors", templateParameters.find(TemplateParameter.URI_ROOT));
        Assert.assertEquals(true, templateParameters.find(TemplateParameter.USE_CQRS));

        final Queries queries =
                templateParameters.find(TemplateParameter.QUERIES);

        Assert.assertEquals("AuthorQueries", queries.getProtocolName());
        Assert.assertEquals("AuthorQueriesActor", queries.getActorName());
        Assert.assertEquals("authorQueries", queries.getAttributeName());

        final List<RouteDeclaration> routeDeclarations =
                templateParameters.find(TemplateParameter.ROUTE_DECLARATIONS);

        final RouteDeclaration nameUpdateRouteDeclaration =
                routeDeclarations.stream().filter(parameter ->
                        parameter.getHandlerName().equals("withName"))
                        .findFirst().get();

        Assert.assertEquals("AuthorData", nameUpdateRouteDeclaration.getBodyType());
        Assert.assertEquals("io.vlingo.xoom.http.resource.ResourceBuilder.post", nameUpdateRouteDeclaration.getBuilderMethod());
        Assert.assertEquals("/authors", nameUpdateRouteDeclaration.getPath());
    }

    @Test
    public void testThatRestResourceIsGeneratedWithoutCQRS() {
        final CodeGenerationParameter packageParameter =
                CodeGenerationParameter.of(PACKAGE, "io.vlingo.xoomapp");

        final CodeGenerationParameter useCQRSParameter =
                CodeGenerationParameter.of(CQRS, "false");

        final CodeGenerationParameter language =
                CodeGenerationParameter.of(LANGUAGE, Language.JAVA);

        final CodeGenerationParameters parameters =
                CodeGenerationParameters.from(packageParameter, language,
                        useCQRSParameter, authorAggregate());

        final List<TemplateData> templatesData =
                RestResourceTemplateDataFactory.build(parameters, contents());

        Assert.assertEquals(1, templatesData.size());

        final TemplateParameters templateParameters = templatesData.get(0).parameters();

        Assert.assertTrue(templateParameters.hasImport("io.vlingo.xoomapp.model.author.Author"));
        Assert.assertTrue(templateParameters.hasImport("io.vlingo.xoomapp.model.author.AuthorEntity"));
        Assert.assertTrue(templateParameters.hasImport("io.vlingo.xoomapp.infrastructure.AuthorData"));
        Assert.assertFalse(templateParameters.hasImport("io.vlingo.xoomapp.infrastructure.persistence.AuthorQueries"));
        Assert.assertEquals("io.vlingo.xoomapp.infrastructure.resource", templateParameters.find(TemplateParameter.PACKAGE_NAME));
        Assert.assertEquals("AuthorResource", templateParameters.find(TemplateParameter.REST_RESOURCE_NAME));
        Assert.assertEquals("Author", templateParameters.find(TemplateParameter.MODEL_PROTOCOL));
        Assert.assertEquals("AuthorEntity", templateParameters.find(TemplateParameter.MODEL_ACTOR));
        Assert.assertEquals("/authors", templateParameters.find(TemplateParameter.URI_ROOT));
        Assert.assertEquals(false, templateParameters.find(TemplateParameter.USE_CQRS));

        final Queries queries =
                templateParameters.find(TemplateParameter.QUERIES);

        Assert.assertTrue(queries.isEmpty());

        final List<RouteDeclaration> routeDeclarations =
                templateParameters.find(TemplateParameter.ROUTE_DECLARATIONS);

        final RouteDeclaration nameUpdateRouteDeclaration =
                routeDeclarations.stream().filter(parameter ->
                        parameter.getHandlerName().equals("withName"))
                        .findFirst().get();

        Assert.assertEquals("AuthorData", nameUpdateRouteDeclaration.getBodyType());
        Assert.assertEquals("io.vlingo.xoom.http.resource.ResourceBuilder.post", nameUpdateRouteDeclaration.getBuilderMethod());
        Assert.assertEquals("/authors", nameUpdateRouteDeclaration.getPath());
    }

    private CodeGenerationParameter authorAggregate() {
        final CodeGenerationParameter idField =
                CodeGenerationParameter.of(Label.STATE_FIELD, "id")
                        .relate(Label.FIELD_TYPE, "String");

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

    private List<Content> contents() {
        return Arrays.asList(
                Content.with(AGGREGATE_PROTOCOL, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "Author.java"), null, null, AUTHOR_CONTENT_TEXT),
                Content.with(DesignerTemplateStandard.AGGREGATE, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "AuthorEntity.java"), null, null, AUTHOR_AGGREGATE_CONTENT_TEXT),
                Content.with(AGGREGATE_STATE, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "AuthorState.java"), null, null, AUTHOR_STATE_CONTENT_TEXT),
                Content.with(DATA_OBJECT, new OutputFile(Paths.get(INFRASTRUCTURE_PACKAGE_PATH).toString(), "AuthorData.java"), null, null, AUTHOR_DATA_CONTENT_TEXT),
                Content.with(QUERIES, new OutputFile(Paths.get(PERSISTENCE_PACKAGE_PATH).toString(), "AuthorQueries.java"), null, null, AUTHOR_QUERIES_CONTENT_TEXT),
                Content.with(QUERIES_ACTOR, new OutputFile(Paths.get(PERSISTENCE_PACKAGE_PATH).toString(), "AuthorQueriesActor.java"), null, null, AUTHOR_QUERIES_ACTOR_CONTENT_TEXT),
                Content.with(STORE_PROVIDER, new OutputFile(Paths.get(PERSISTENCE_PACKAGE_PATH).toString(), "QueryModelStateStoreProvider.java"), null, null, QUERY_MODEL_STORE_PROVIDER_CONTENT)
        );
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

    private static final String AUTHOR_STATE_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.author; \\n" +
                    "public class AuthorState { \\n" +
                    "... \\n" +
                    "}";

    private static final String AUTHOR_AGGREGATE_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.author; \\n" +
                    "public class AuthorEntity { \\n" +
                    "... \\n" +
                    "}";

    private static final String AUTHOR_DATA_CONTENT_TEXT =
            "package io.vlingo.xoomapp.infrastructure; \\n" +
                    "public class AuthorData { \\n" +
                    "... \\n" +
                    "}";

    private static final String AUTHOR_QUERIES_CONTENT_TEXT =
            "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                    "public interface AuthorQueries { \\n" +
                    "... \\n" +
                    "}";

    private static final String AUTHOR_QUERIES_ACTOR_CONTENT_TEXT =
            "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                    "public class AuthorQueriesActor { \\n" +
                    "... \\n" +
                    "}";

    private static final String QUERY_MODEL_STORE_PROVIDER_CONTENT =
            "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                    "public class QueryModelStateStoreProvider { \\n" +
                    "... \\n" +
                    "}";
}
