// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.codegen.template.storage;

import io.vlingo.xoom.turbo.OperatingSystem;
import io.vlingo.xoom.turbo.codegen.content.Content;
import io.vlingo.xoom.turbo.codegen.parameter.ImportParameter;
import io.vlingo.xoom.turbo.codegen.template.OutputFile;
import io.vlingo.xoom.turbo.codegen.template.TemplateData;
import io.vlingo.xoom.turbo.codegen.template.TemplateParameters;
import io.vlingo.xoom.turbo.codegen.template.projections.ProjectionType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.turbo.codegen.template.TemplateStandard.*;
import static io.vlingo.xoom.turbo.codegen.template.projections.ProjectionType.EVENT_BASED;
import static io.vlingo.xoom.turbo.codegen.template.storage.Model.*;
import static io.vlingo.xoom.turbo.codegen.template.storage.StorageType.JOURNAL;

public class StorageTemplateDataFactoryTest {

    @Test
    public void testStorageTemplateDataOnSourcedSingleModel() {
        final List<TemplateData> allTemplatesData =
                StorageTemplateDataFactory.build("io.vlingo.xoomapp", "xoomapp", contents(),
                        JOURNAL, databaseTypes(), EVENT_BASED, false, false, false);

        //General Assert

        Assert.assertEquals(4, allTemplatesData.size());
        Assert.assertEquals(2, allTemplatesData.stream().filter(templateData -> templateData.hasStandard(ADAPTER)).count());
        Assert.assertEquals(1, allTemplatesData.stream().filter(templateData -> templateData.hasStandard(STORE_PROVIDER)).count());

        //Assert for StateAdapter

        final TemplateData entryAdapterTemplateData =
                allTemplatesData.stream().filter(templateData -> templateData.hasStandard(ADAPTER)).findFirst().get();

        final TemplateParameters stateAdapterConfigurationParameters =
                entryAdapterTemplateData.parameters();

        Assert.assertEquals(EXPECTED_PACKAGE, stateAdapterConfigurationParameters.find(PACKAGE_NAME));
        Assert.assertEquals("BookRented", stateAdapterConfigurationParameters.find(SOURCE_NAME));
        Assert.assertEquals(JOURNAL, stateAdapterConfigurationParameters.find(STORAGE_TYPE));
        Assert.assertEquals(1, stateAdapterConfigurationParameters.<Set<ImportParameter>>find(IMPORTS).size());
        Assert.assertTrue(stateAdapterConfigurationParameters.hasImport("io.vlingo.xoomapp.model.book.BookRented"));
        Assert.assertEquals("BookRentedAdapter", entryAdapterTemplateData.filename());

        //Assert for StoreProvider

        final TemplateData storeProviderTemplateData =
                allTemplatesData.stream().filter(templateData -> templateData.hasStandard(STORE_PROVIDER)).findFirst().get();

        final TemplateParameters storeProviderParameters = storeProviderTemplateData.parameters();

        Assert.assertEquals(EXPECTED_PACKAGE, storeProviderParameters.find(PACKAGE_NAME));
        Assert.assertEquals(DOMAIN, storeProviderParameters.find(MODEL));
        Assert.assertEquals("JournalProvider", storeProviderParameters.find(STORE_PROVIDER_NAME));
        Assert.assertEquals(4, storeProviderParameters.<Set<ImportParameter>>find(IMPORTS).size());
        Assert.assertTrue(storeProviderParameters.hasImport("io.vlingo.xoomapp.model.book.BookRented"));
        Assert.assertTrue(storeProviderParameters.hasImport("io.vlingo.xoomapp.model.book.BookPurchased"));
        Assert.assertTrue(storeProviderParameters.hasImport("io.vlingo.xoomapp.model.author.AuthorEntity"));
        Assert.assertTrue(storeProviderParameters.hasImport("io.vlingo.xoomapp.model.book.BookEntity"));
        Assert.assertEquals("BookRented", storeProviderParameters.<List<Adapter>>find(ADAPTERS).get(0).getSourceClass());
        Assert.assertEquals("BookRentedAdapter", storeProviderParameters.<List<Adapter>>find(ADAPTERS).get(0).getAdapterClass());
        Assert.assertEquals("BookPurchased", storeProviderParameters.<List<Adapter>>find(ADAPTERS).get(1).getSourceClass());
        Assert.assertEquals("BookPurchasedAdapter", storeProviderParameters.<List<Adapter>>find(ADAPTERS).get(1).getAdapterClass());
        Assert.assertEquals(2, storeProviderParameters.<Set<String>>find(AGGREGATES).size());
        Assert.assertTrue(storeProviderParameters.<Set<String>>find(AGGREGATES).contains("AuthorEntity"));
        Assert.assertTrue(storeProviderParameters.<Set<String>>find(AGGREGATES).contains("BookEntity"));
        Assert.assertEquals("JournalProvider", storeProviderTemplateData.filename());
    }

    @Test
    public void testStorageTemplateDataOnStatefulSingleModel() {
        final List<TemplateData> allTemplatesData =
                StorageTemplateDataFactory.build("io.vlingo.xoomapp", "xoomapp", contents(),
                        StorageType.STATE_STORE, databaseTypes(), EVENT_BASED, false, false, false);

        //General Assert

        Assert.assertEquals(4, allTemplatesData.size());
        Assert.assertEquals(2, allTemplatesData.stream().filter(templateData -> templateData.hasStandard(ADAPTER)).count());
        Assert.assertEquals(1, allTemplatesData.stream().filter(templateData -> templateData.hasStandard(STORE_PROVIDER)).count());

        //Assert for StateAdapter

        final TemplateData stateAdapterTemplateData =
                allTemplatesData.stream().filter(templateData -> templateData.hasStandard(ADAPTER)).findFirst().get();

        final TemplateParameters stateAdapterConfigurationParameters =
                stateAdapterTemplateData.parameters();

        Assert.assertEquals(EXPECTED_PACKAGE, stateAdapterConfigurationParameters.find(PACKAGE_NAME));
        Assert.assertEquals("BookState", stateAdapterConfigurationParameters.find(SOURCE_NAME));
        Assert.assertEquals(StorageType.STATE_STORE, stateAdapterConfigurationParameters.find(STORAGE_TYPE));
        Assert.assertEquals(1, stateAdapterConfigurationParameters.<Set<ImportParameter>>find(IMPORTS).size());
        Assert.assertTrue(stateAdapterConfigurationParameters.hasImport("io.vlingo.xoomapp.model.book.BookState"));
        Assert.assertEquals("BookStateAdapter", stateAdapterTemplateData.filename());

        //Assert for StoreProvider

        final TemplateData storeProviderTemplateData =
                allTemplatesData.stream().filter(templateData -> templateData.hasStandard(STORE_PROVIDER)).findFirst().get();

        final TemplateParameters storeProviderParameters = storeProviderTemplateData.parameters();

        Assert.assertEquals(EXPECTED_PACKAGE, storeProviderParameters.find(PACKAGE_NAME));
        Assert.assertEquals(DOMAIN, storeProviderParameters.find(MODEL));
        Assert.assertEquals("StateStoreProvider", storeProviderParameters.find(STORE_PROVIDER_NAME));
        Assert.assertEquals(2, storeProviderParameters.<Set<ImportParameter>>find(IMPORTS).size());
        Assert.assertTrue(storeProviderParameters.hasImport("io.vlingo.xoomapp.model.author.AuthorState"));
        Assert.assertTrue(storeProviderParameters.hasImport("io.vlingo.xoomapp.model.book.BookState"));
        Assert.assertEquals("BookState", storeProviderParameters.<List<Adapter>>find(ADAPTERS).get(0).getSourceClass());
        Assert.assertEquals("BookStateAdapter", storeProviderParameters.<List<Adapter>>find(ADAPTERS).get(0).getAdapterClass());
        Assert.assertEquals("AuthorState", storeProviderParameters.<List<Adapter>>find(ADAPTERS).get(1).getSourceClass());
        Assert.assertEquals("AuthorStateAdapter", storeProviderParameters.<List<Adapter>>find(ADAPTERS).get(1).getAdapterClass());
        Assert.assertEquals("StateStoreProvider", storeProviderTemplateData.filename());
    }

    @Test
    public void testStorageTemplateDataOnStatefulCQRSModel() {
        final List<TemplateData> allTemplatesData =
                StorageTemplateDataFactory.build("io.vlingo.xoomapp", "xoomapp", contents(),
                        StorageType.STATE_STORE, databaseTypesForCQRS(), ProjectionType.NONE, false, false, true);

        //General Assert

        Assert.assertEquals(9, allTemplatesData.size());
        Assert.assertEquals(2, allTemplatesData.stream().filter(templateData -> templateData.hasStandard(ADAPTER)).count());
        Assert.assertEquals(2, allTemplatesData.stream().filter(templateData -> templateData.hasStandard(STORE_PROVIDER)).count());

        //Assert for StateAdapter

        final TemplateData stateAdapterTemplateData =
                allTemplatesData.stream().filter(templateData -> templateData.hasStandard(ADAPTER)).findFirst().get();

        final TemplateParameters stateAdapterConfigurationParameters =
                stateAdapterTemplateData.parameters();

        Assert.assertEquals(EXPECTED_PACKAGE, stateAdapterConfigurationParameters.find(PACKAGE_NAME));
        Assert.assertEquals("BookState", stateAdapterConfigurationParameters.find(SOURCE_NAME));
        Assert.assertEquals(StorageType.STATE_STORE, stateAdapterConfigurationParameters.find(STORAGE_TYPE));
        Assert.assertEquals(1, stateAdapterConfigurationParameters.<Set<ImportParameter>>find(IMPORTS).size());
        Assert.assertTrue(stateAdapterConfigurationParameters.hasImport("io.vlingo.xoomapp.model.book.BookState"));
        Assert.assertEquals("BookStateAdapter", stateAdapterTemplateData.filename());

        //Assert for StoreProvider

        final List<TemplateData> storeProviders =
                allTemplatesData.stream()
                        .filter(templateData -> templateData.hasStandard(STORE_PROVIDER))
                        .collect(Collectors.toList());

        IntStream.range(0, 1).forEach(modelClassificationIndex -> {
            final TemplateData storeProviderTemplateData = storeProviders.get(modelClassificationIndex);
            final Model model = modelClassificationIndex == 0 ? COMMAND : QUERY;
            final TemplateParameters storeProviderParameters = storeProviderTemplateData.parameters();
            final int expectedImports = modelClassificationIndex == 0 ? 2 : 1;
            Assert.assertEquals(EXPECTED_PACKAGE, storeProviderParameters.find(PACKAGE_NAME));
            Assert.assertEquals(model, storeProviderParameters.find(MODEL));
            Assert.assertEquals(model.title + "StateStoreProvider", storeProviderParameters.find(STORE_PROVIDER_NAME));
            Assert.assertEquals(expectedImports, storeProviderParameters.<Set<ImportParameter>>find(IMPORTS).size());
            Assert.assertTrue(storeProviderParameters.hasImport("io.vlingo.xoomapp.model.author.AuthorState"));
            Assert.assertTrue(storeProviderParameters.hasImport("io.vlingo.xoomapp.model.book.BookState"));
            Assert.assertEquals("BookState", storeProviderParameters.<List<Adapter>>find(ADAPTERS).get(0).getSourceClass());
            Assert.assertEquals("BookStateAdapter", storeProviderParameters.<List<Adapter>>find(ADAPTERS).get(0).getAdapterClass());
            Assert.assertEquals("AuthorState", storeProviderParameters.<List<Adapter>>find(ADAPTERS).get(1).getSourceClass());
            Assert.assertEquals("AuthorStateAdapter", storeProviderParameters.<List<Adapter>>find(ADAPTERS).get(1).getAdapterClass());
            Assert.assertEquals(model.title  + "StateStoreProvider", storeProviderTemplateData.filename());
        });
    }

    private List<Content> contents() {
        return Arrays.asList(
                    Content.with(AGGREGATE_STATE, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "AuthorState.java"), null, null, AUTHOR_STATE_CONTENT_TEXT),
                    Content.with(AGGREGATE_STATE, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookState.java"), null, null, BOOK_STATE_CONTENT_TEXT),
                    Content.with(DOMAIN_EVENT, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookRented.java"), null, null, BOOK_RENTED_TEXT),
                    Content.with(DOMAIN_EVENT, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookPurchased.java"), null, null, BOOK_PURCHASED_TEXT),
                    Content.with(AGGREGATE_PROTOCOL, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "Author.java"), null, null, AUTHOR_CONTENT_TEXT),
                    Content.with(AGGREGATE_PROTOCOL, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "Book.java"), null, null, BOOK_CONTENT_TEXT),
                    Content.with(AGGREGATE, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "AuthorEntity.java"), null, null, AUTHOR_ENTITY_CONTENT_TEXT),
                    Content.with(AGGREGATE, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookEntity.java"), null, null, BOOK_ENTITY_CONTENT_TEXT),
                    Content.with(PROJECTION_DISPATCHER_PROVIDER, new OutputFile(PERSISTENCE_PACKAGE_PATH, "ProjectionDispatcherProvider.java"), null, null, PROJECTION_DISPATCHER_PROVIDER_CONTENT_TEXT),
                    Content.with(DATA_OBJECT, new OutputFile(Paths.get(INFRASTRUCTURE_PACKAGE_PATH).toString(), "AuthorData.java"), null, null, AUTHOR_DATA_CONTENT_TEXT),
                    Content.with(DATA_OBJECT, new OutputFile(Paths.get(INFRASTRUCTURE_PACKAGE_PATH).toString(), "BookData.java"), null, null, BOOK_DATA_CONTENT_TEXT)
                );
    }

    private static Map<Model, DatabaseType> databaseTypesForCQRS() {
        return new HashMap<Model, DatabaseType>() {{
           put(COMMAND, DatabaseType.HSQLDB);
           put(QUERY, DatabaseType.IN_MEMORY);
        }};
    }

    private static Map<Model, DatabaseType> databaseTypes() {
        return new HashMap<Model, DatabaseType>() {{
            put(DOMAIN, DatabaseType.POSTGRES);
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

    private static final String PERSISTENCE_PACKAGE_PATH =
            Paths.get(PROJECT_PATH, "src", "main", "java",
                    "io", "vlingo", "xoomapp", "infrastructure", "persistence").toString();

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

    private static final String AUTHOR_ENTITY_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.author; \\n" +
                    "public interface AuthorEntity { \\n" +
                    "... \\n" +
                    "}";

    private static final String BOOK_ENTITY_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.book; \\n" +
                    "public interface BookEntity { \\n" +
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

    private static final String PROJECTION_DISPATCHER_PROVIDER_CONTENT_TEXT =
            "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                    "public class ProjectionDispatcherProvider { \\n" +
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
