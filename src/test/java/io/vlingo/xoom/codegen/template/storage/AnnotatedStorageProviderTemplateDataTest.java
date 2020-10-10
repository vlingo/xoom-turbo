// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.storage;

import io.vlingo.xoom.OperatingSystem;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.parameter.ImportParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateFile;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.*;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.QUERIES;
import static io.vlingo.xoom.codegen.template.TemplateStandard.*;
import static io.vlingo.xoom.codegen.template.projections.ProjectionType.EVENT_BASED;
import static io.vlingo.xoom.codegen.template.storage.Model.COMMAND;
import static io.vlingo.xoom.codegen.template.storage.Model.QUERY;

public class AnnotatedStorageProviderTemplateDataTest {

    @Test
    public void testWithAdaptersAndProjections() {
        final List<TemplateData> allTemplatesData =
                StorageTemplateDataFactory.build("io.vlingo.xoomapp", contents(),
                        StorageType.STATE_STORE, databaseTypes(), EVENT_BASED, false, true, true);

        //General Assert
        Assert.assertEquals(8, allTemplatesData.size());
        Assert.assertEquals(2, allTemplatesData.stream().filter(templateData -> templateData.hasStandard(QUERIES)).count());
        Assert.assertEquals(2, allTemplatesData.stream().filter(templateData -> templateData.hasStandard(QUERIES_ACTOR)).count());
        Assert.assertEquals(1, allTemplatesData.stream().filter(templateData -> templateData.hasStandard(STORE_PROVIDER)).count());

        //Assert for Queries
        final TemplateData queriesTemplateData =
                allTemplatesData.stream().filter(templateData -> templateData.hasStandard(QUERIES)).findFirst().get();

        final TemplateParameters queriesParameters =
                queriesTemplateData.parameters();

        Assert.assertEquals(EXPECTED_PACKAGE, queriesParameters.find(PACKAGE_NAME));
        Assert.assertEquals("BookQueries", queriesParameters.find(QUERIES_NAME));
        Assert.assertEquals("BookData", queriesParameters.find(ENTITY_DATA_NAME));
        Assert.assertEquals("bookOf", queriesParameters.find(QUERY_ID_METHOD_NAME));
        Assert.assertEquals("books", queriesParameters.find(QUERY_ALL_METHOD_NAME));
        Assert.assertEquals(1, queriesParameters.<Set<ImportParameter>>find(IMPORTS).size());
        Assert.assertTrue(queriesParameters.hasImport("io.vlingo.xoomapp.infrastructure.BookData"));

        //Assert for QueriesActor
        final TemplateData queriesActorTemplateData =
                allTemplatesData.stream().filter(templateData -> templateData.hasStandard(QUERIES_ACTOR)).findFirst().get();

        final TemplateParameters queriesActorParameters =
                queriesActorTemplateData.parameters();

        Assert.assertEquals(EXPECTED_PACKAGE, queriesActorParameters.find(PACKAGE_NAME));
        Assert.assertEquals("BookQueriesActor", queriesActorParameters.find(QUERIES_ACTOR_NAME));

        //Assert for StoreProvider
        final TemplateData storeProviderTemplateData =
                allTemplatesData.stream().filter(templateData -> templateData.hasStandard(STORE_PROVIDER)).findFirst().get();

        final TemplateParameters storeProviderParameters = storeProviderTemplateData.parameters();

        Assert.assertEquals(EXPECTED_PACKAGE, storeProviderParameters.find(PACKAGE_NAME));
        Assert.assertEquals("io.vlingo.xoomapp", storeProviderParameters.find(BASE_PACKAGE));
        Assert.assertEquals("PersistenceSetup", storeProviderParameters.find(STORE_PROVIDER_NAME));
        Assert.assertEquals(5, storeProviderParameters.<Set<ImportParameter>>find(IMPORTS).size());
        Assert.assertTrue(storeProviderParameters.hasImport("io.vlingo.xoomapp.model.author.AuthorState"));
        Assert.assertTrue(storeProviderParameters.hasImport("io.vlingo.xoomapp.model.book.BookState"));
        Assert.assertTrue(storeProviderParameters.hasImport("io.vlingo.xoomapp.model.author.AuthorRated"));
        Assert.assertTrue(storeProviderParameters.hasImport("io.vlingo.xoomapp.model.book.BookRented"));
        Assert.assertTrue(storeProviderParameters.hasImport("io.vlingo.xoomapp.model.book.BookPurchased"));
        Assert.assertEquals("BookState", storeProviderParameters.<List<AdapterParameter>>find(ADAPTERS).get(0).getSourceClass());
        Assert.assertEquals(false, storeProviderParameters.<List<AdapterParameter>>find(ADAPTERS).get(0).isLast());
        Assert.assertEquals("AuthorState", storeProviderParameters.<List<AdapterParameter>>find(ADAPTERS).get(1).getSourceClass());
        Assert.assertEquals(true, storeProviderParameters.<List<AdapterParameter>>find(ADAPTERS).get(1).isLast());
        Assert.assertEquals("AuthorProjectionActor", storeProviderParameters.<List<ProjectionParameter>>find(PROJECTIONS).get(0).getActor());
        Assert.assertEquals("AuthorRated.class", storeProviderParameters.<List<ProjectionParameter>>find(PROJECTIONS).get(0).getCauses());
        Assert.assertEquals(false, storeProviderParameters.<List<ProjectionParameter>>find(PROJECTIONS).get(0).isLast());
        Assert.assertEquals("BookProjectionActor", storeProviderParameters.<List<ProjectionParameter>>find(PROJECTIONS).get(1).getActor());
        Assert.assertEquals("BookRented.class, BookPurchased.class", storeProviderParameters.<List<ProjectionParameter>>find(PROJECTIONS).get(1).getCauses());
        Assert.assertEquals(true, storeProviderParameters.<List<ProjectionParameter>>find(PROJECTIONS).get(1).isLast());
        Assert.assertEquals("PersistenceSetup.java", storeProviderTemplateData.filename());
        Assert.assertEquals(true, storeProviderParameters.find(REQUIRE_ADAPTERS));
        Assert.assertEquals(true, storeProviderParameters.find(USE_PROJECTIONS));
        Assert.assertEquals(true, storeProviderParameters.find(USE_ANNOTATIONS));
        Assert.assertEquals(true, storeProviderParameters.find(USE_CQRS));
    }

    private List<Content> contents() {
        return Arrays.asList(
                Content.with(AGGREGATE_STATE, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "AuthorState.java"), null, null, AUTHOR_STATE_CONTENT_TEXT),
                Content.with(DOMAIN_EVENT, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "AuthorRated.java"), null, null, AUTHOR_STATE_CONTENT_TEXT),
                Content.with(AGGREGATE_STATE, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookState.java"), null, null, BOOK_STATE_CONTENT_TEXT),
                Content.with(DOMAIN_EVENT, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookRented.java"), null, null, BOOK_RENTED_TEXT),
                Content.with(DOMAIN_EVENT, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookPurchased.java"), null, null, BOOK_PURCHASED_TEXT),
                Content.with(AGGREGATE_PROTOCOL, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "Author.java"), null, null, AUTHOR_CONTENT_TEXT),
                Content.with(AGGREGATE_PROTOCOL, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "Book.java"), null, null, BOOK_CONTENT_TEXT),
                Content.with(ENTITY_DATA, new TemplateFile(Paths.get(INFRASTRUCTURE_PACKAGE_PATH).toString(), "AuthorData.java"), null, null, AUTHOR_DATA_CONTENT_TEXT),
                Content.with(ENTITY_DATA, new TemplateFile(Paths.get(INFRASTRUCTURE_PACKAGE_PATH).toString(), "BookData.java"), null, null, BOOK_DATA_CONTENT_TEXT)
            );
    }

    private static final Map<Model, DatabaseType> databaseTypes() {
        return new HashMap<Model, DatabaseType>() {{
            put(COMMAND, DatabaseType.HSQLDB);
            put(QUERY, DatabaseType.IN_MEMORY);
        }};
    }

    private static final String EXPECTED_PACKAGE = "io.vlingo.xoomapp.infrastructure.persistence";

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

    private static final String BOOK_RENTED_TEXT =
            "package io.vlingo.xoomapp.model.book; \\n" +
                    "public class BookRented extends Event { \\n" +
                    "... \\n" +
                    "}";

    private static final String BOOK_PURCHASED_TEXT =
            "package io.vlingo.xoomapp.model.book; \\n" +
                    "public class BookPurchased extends Event { \\n" +
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
}
