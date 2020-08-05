// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.storage;

import io.vlingo.xoom.OperatingSystem;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.file.ImportParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateFile;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.projections.ProjectionType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.*;
import static io.vlingo.xoom.codegen.template.projections.ProjectionType.EVENT_BASED;
import static io.vlingo.xoom.codegen.template.storage.ModelClassification.*;
import static io.vlingo.xoom.codegen.template.storage.StorageType.JOURNAL;

public class StorageTemplateDataFactoryTest {

    @Test
    public void testStorageTemplateDataOnSourcedSingleModel() {
        final List<TemplateData> allTemplatesData =
                StorageTemplateDataFactory.build("io.vlingo.xoomapp", contents(),
                        JOURNAL, databaseTypes(), EVENT_BASED, false, false);

        //General Assert

        Assert.assertEquals(3, allTemplatesData.size());
        Assert.assertEquals(2, allTemplatesData.stream().filter(templateData -> templateData.standard().equals(ADAPTER)).count());
        Assert.assertEquals(1, allTemplatesData.stream().filter(templateData -> templateData.standard().equals(STORE_PROVIDER)).count());

        //Assert for StateAdapter

        final TemplateData entryAdapterTemplateData =
                allTemplatesData.stream().filter(templateData -> templateData.standard().equals(ADAPTER)).findFirst().get();

        final TemplateParameters stateAdapterConfigurationParameters =
                entryAdapterTemplateData.parameters();

        Assert.assertEquals(EXPECTED_PACKAGE, stateAdapterConfigurationParameters.find(PACKAGE_NAME));
        Assert.assertEquals("BookRented", stateAdapterConfigurationParameters.find(SOURCE_NAME));
        Assert.assertEquals(JOURNAL, stateAdapterConfigurationParameters.find(STORAGE_TYPE));
        Assert.assertEquals(1, stateAdapterConfigurationParameters.<List<ImportParameter>>find(IMPORTS).size());
        Assert.assertEquals("io.vlingo.xoomapp.model.book.BookRented", stateAdapterConfigurationParameters.<List<ImportParameter>>find(IMPORTS).get(0).getQualifiedClassName());
        Assert.assertEquals("BookRentedAdapter.java", entryAdapterTemplateData.filename());

        //Assert for StoreProvider

        final TemplateData storeProviderTemplateData =
                allTemplatesData.stream().filter(templateData -> templateData.standard().equals(STORE_PROVIDER)).findFirst().get();

        final TemplateParameters storeProviderParameters = storeProviderTemplateData.parameters();

        Assert.assertEquals(EXPECTED_PACKAGE, storeProviderParameters.find(PACKAGE_NAME));
        Assert.assertEquals(SINGLE, storeProviderParameters.find(MODEL_CLASSIFICATION));
        Assert.assertEquals("JournalProvider", storeProviderParameters.find(STORAGE_PROVIDER_NAME));
        Assert.assertEquals("io.vlingo.symbio.store.journal.jdbc.JDBCJournalActor", storeProviderParameters.find(STORE_NAME));
        Assert.assertEquals(3, storeProviderParameters.<List<ImportParameter>>find(IMPORTS).size());
        Assert.assertEquals("io.vlingo.xoomapp.model.book.BookRented", storeProviderParameters.<List<ImportParameter>>find(IMPORTS).get(0).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.xoomapp.model.book.BookPurchased", storeProviderParameters.<List<ImportParameter>>find(IMPORTS).get(1).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.symbio.store.common.jdbc.postgres.PostgresConfigurationProvider", storeProviderParameters.<List<ImportParameter>>find(IMPORTS).get(2).getQualifiedClassName());
        Assert.assertEquals("PostgresConfigurationProvider", storeProviderParameters.find(CONFIGURATION_PROVIDER_NAME));
        Assert.assertEquals("BookRented", storeProviderParameters.<List<AdapterParameter>>find(ADAPTERS).get(0).getSourceClass());
        Assert.assertEquals("BookRentedAdapter", storeProviderParameters.<List<AdapterParameter>>find(ADAPTERS).get(0).getAdapterClass());
        Assert.assertEquals("BookPurchased", storeProviderParameters.<List<AdapterParameter>>find(ADAPTERS).get(1).getSourceClass());
        Assert.assertEquals("BookPurchasedAdapter", storeProviderParameters.<List<AdapterParameter>>find(ADAPTERS).get(1).getAdapterClass());
        Assert.assertEquals("JournalProvider.java", storeProviderTemplateData.filename());
    }

    @Test
    public void testStorageTemplateDataOnStatefulSingleModel() {
        final List<TemplateData> allTemplatesData =
                StorageTemplateDataFactory.build("io.vlingo.xoomapp", contents(),
                        StorageType.STATE_STORE, databaseTypes(), EVENT_BASED, false, false);

        //General Assert

        Assert.assertEquals(3, allTemplatesData.size());
        Assert.assertEquals(2, allTemplatesData.stream().filter(templateData -> templateData.standard().equals(ADAPTER)).count());
        Assert.assertEquals(1, allTemplatesData.stream().filter(templateData -> templateData.standard().equals(STORE_PROVIDER)).count());

        //Assert for StateAdapter

        final TemplateData stateAdapterTemplateData =
                allTemplatesData.stream().filter(templateData -> templateData.standard().equals(ADAPTER)).findFirst().get();

        final TemplateParameters stateAdapterConfigurationParameters =
                stateAdapterTemplateData.parameters();

        Assert.assertEquals(EXPECTED_PACKAGE, stateAdapterConfigurationParameters.find(PACKAGE_NAME));
        Assert.assertEquals("AuthorState", stateAdapterConfigurationParameters.find(SOURCE_NAME));
        Assert.assertEquals(StorageType.STATE_STORE, stateAdapterConfigurationParameters.find(STORAGE_TYPE));
        Assert.assertEquals(1, stateAdapterConfigurationParameters.<List<ImportParameter>>find(IMPORTS).size());
        Assert.assertEquals("io.vlingo.xoomapp.model.author.AuthorState", stateAdapterConfigurationParameters.<List<ImportParameter>>find(IMPORTS).get(0).getQualifiedClassName());
        Assert.assertEquals("AuthorStateAdapter.java", stateAdapterTemplateData.filename());

        //Assert for StoreProvider

        final TemplateData storeProviderTemplateData =
                allTemplatesData.stream().filter(templateData -> templateData.standard().equals(STORE_PROVIDER)).findFirst().get();

        final TemplateParameters storeProviderParameters = storeProviderTemplateData.parameters();

        Assert.assertEquals(EXPECTED_PACKAGE, storeProviderParameters.find(PACKAGE_NAME));
        Assert.assertEquals(SINGLE, storeProviderParameters.find(MODEL_CLASSIFICATION));
        Assert.assertEquals("StateStoreProvider", storeProviderParameters.find(STORAGE_PROVIDER_NAME));
        Assert.assertEquals("io.vlingo.symbio.store.state.jdbc.JDBCStateStoreActor", storeProviderParameters.find(STORE_NAME));
        Assert.assertEquals(4, storeProviderParameters.<List<ImportParameter>>find(IMPORTS).size());
        Assert.assertEquals("io.vlingo.xoomapp.model.author.AuthorState", storeProviderParameters.<List<ImportParameter>>find(IMPORTS).get(0).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.xoomapp.model.book.BookState", storeProviderParameters.<List<ImportParameter>>find(IMPORTS).get(1).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.symbio.store.state.jdbc.postgres.PostgresStorageDelegate", storeProviderParameters.<List<ImportParameter>>find(IMPORTS).get(2).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.symbio.store.common.jdbc.postgres.PostgresConfigurationProvider", storeProviderParameters.<List<ImportParameter>>find(IMPORTS).get(3).getQualifiedClassName());
        Assert.assertEquals("PostgresStorageDelegate", storeProviderParameters.find(STORAGE_DELEGATE_NAME));
        Assert.assertEquals("PostgresConfigurationProvider", storeProviderParameters.find(CONFIGURATION_PROVIDER_NAME));
        Assert.assertEquals("AuthorState", storeProviderParameters.<List<AdapterParameter>>find(ADAPTERS).get(0).getSourceClass());
        Assert.assertEquals("AuthorStateAdapter", storeProviderParameters.<List<AdapterParameter>>find(ADAPTERS).get(0).getAdapterClass());
        Assert.assertEquals("BookState", storeProviderParameters.<List<AdapterParameter>>find(ADAPTERS).get(1).getSourceClass());
        Assert.assertEquals("BookStateAdapter", storeProviderParameters.<List<AdapterParameter>>find(ADAPTERS).get(1).getAdapterClass());
        Assert.assertEquals("StateStoreProvider.java", storeProviderTemplateData.filename());
    }

    @Test
    public void testStorageTemplateDataOnStatefulCQRSModel() {
        final List<TemplateData> allTemplatesData =
                StorageTemplateDataFactory.build("io.vlingo.xoomapp", contents(),
                        StorageType.STATE_STORE, databaseTypesForCQRS(), ProjectionType.NONE, false, false);

        //General Assert

        Assert.assertEquals(4, allTemplatesData.size());
        Assert.assertEquals(2, allTemplatesData.stream().filter(templateData -> templateData.standard().equals(ADAPTER)).count());
        Assert.assertEquals(2, allTemplatesData.stream().filter(templateData -> templateData.standard().equals(STORE_PROVIDER)).count());

        //Assert for StateAdapter

        final TemplateData stateAdapterTemplateData =
                allTemplatesData.stream().filter(templateData -> templateData.standard().equals(ADAPTER)).findFirst().get();

        final TemplateParameters stateAdapterConfigurationParameters =
                stateAdapterTemplateData.parameters();

        Assert.assertEquals(EXPECTED_PACKAGE, stateAdapterConfigurationParameters.find(PACKAGE_NAME));
        Assert.assertEquals("AuthorState", stateAdapterConfigurationParameters.find(SOURCE_NAME));
        Assert.assertEquals(StorageType.STATE_STORE, stateAdapterConfigurationParameters.find(STORAGE_TYPE));
        Assert.assertEquals(1, stateAdapterConfigurationParameters.<List<ImportParameter>>find(IMPORTS).size());
        Assert.assertEquals("io.vlingo.xoomapp.model.author.AuthorState", stateAdapterConfigurationParameters.<List<ImportParameter>>find(IMPORTS).get(0).getQualifiedClassName());
        Assert.assertEquals("AuthorStateAdapter.java", stateAdapterTemplateData.filename());

        //Assert for StoreProvider

        final List<TemplateData> storeProviders =
                allTemplatesData.stream()
                        .filter(templateData -> templateData.standard().equals(STORE_PROVIDER))
                        .collect(Collectors.toList());

        IntStream.range(0, 1).forEach(modelClassificationIndex -> {
            final TemplateData storeProviderTemplateData = storeProviders.get(modelClassificationIndex);
            final ModelClassification modelClassification = modelClassificationIndex == 0 ? COMMAND : QUERY;
            final TemplateParameters storeProviderParameters = storeProviderTemplateData.parameters();
            final String expectedStateStoreActor = modelClassificationIndex == 0 ? "io.vlingo.symbio.store.state.jdbc.JDBCStateStoreActor" : "io.vlingo.symbio.store.state.jdbc.InMemoryStateStoreActor";
            final int expectedImports = modelClassificationIndex == 0 ? 4 : 2;
            Assert.assertEquals(EXPECTED_PACKAGE, storeProviderParameters.find(PACKAGE_NAME));
            Assert.assertEquals(modelClassification, storeProviderParameters.find(MODEL_CLASSIFICATION));
            Assert.assertEquals(modelClassification.title + "StateStoreProvider", storeProviderParameters.find(STORAGE_PROVIDER_NAME));
            Assert.assertEquals(expectedStateStoreActor, storeProviderParameters.find(STORE_NAME));
            Assert.assertEquals(expectedImports, storeProviderParameters.<List<ImportParameter>>find(IMPORTS).size());
            Assert.assertEquals("io.vlingo.xoomapp.model.author.AuthorState", storeProviderParameters.<List<ImportParameter>>find(IMPORTS).get(0).getQualifiedClassName());
            Assert.assertEquals("io.vlingo.xoomapp.model.book.BookState", storeProviderParameters.<List<ImportParameter>>find(IMPORTS).get(1).getQualifiedClassName());
            Assert.assertEquals("AuthorState", storeProviderParameters.<List<AdapterParameter>>find(ADAPTERS).get(0).getSourceClass());
            Assert.assertEquals("AuthorStateAdapter", storeProviderParameters.<List<AdapterParameter>>find(ADAPTERS).get(0).getAdapterClass());
            Assert.assertEquals("BookState", storeProviderParameters.<List<AdapterParameter>>find(ADAPTERS).get(1).getSourceClass());
            Assert.assertEquals("BookStateAdapter", storeProviderParameters.<List<AdapterParameter>>find(ADAPTERS).get(1).getAdapterClass());
            Assert.assertEquals(modelClassification.title  + "StateStoreProvider.java", storeProviderTemplateData.filename());
        });
    }

    private List<Content> contents() {
        return Arrays.asList(
                    Content.with(STATE, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "AuthorState.java"), null, null, AUTHOR_STATE_CONTENT_TEXT),
                    Content.with(STATE, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookState.java"), null, null, BOOK_STATE_CONTENT_TEXT),
                    Content.with(DOMAIN_EVENT, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookRented.java"), null, null, BOOK_RENTED_TEXT),
                    Content.with(DOMAIN_EVENT, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookPurchased.java"), null, null, BOOK_PURCHASED_TEXT),
                    Content.with(AGGREGATE_PROTOCOL, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "Author.java"), null, null, AUTHOR_CONTENT_TEXT),
                    Content.with(AGGREGATE_PROTOCOL, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "Book.java"), null, null, BOOK_CONTENT_TEXT),
                    Content.with(PROJECTION_DISPATCHER_PROVIDER, new TemplateFile(PERSISTENCE_PACKAGE_PATH, "ProjectionDispatcherProvider.java"), null, null, PROJECTION_DISPATCHER_PROVIDER_CONTENT_TEXT)
                );
    }

    private static final Map<ModelClassification, DatabaseType> databaseTypesForCQRS() {
        return new HashMap<ModelClassification, DatabaseType>() {{
           put(COMMAND, DatabaseType.HSQLDB);
           put(QUERY, DatabaseType.IN_MEMORY);
        }};
    }

    private static final Map<ModelClassification, DatabaseType> databaseTypes() {
        return new HashMap<ModelClassification, DatabaseType>() {{
            put(SINGLE, DatabaseType.POSTGRES);
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

    private static final String PROJECTION_DISPATCHER_PROVIDER_CONTENT_TEXT =
            "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                    "public class ProjectionDispatcherProvider { \\n" +
                    "... \\n" +
                    "}";
}
