// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.steps;

import io.vlingo.xoom.OperatingSystem;
import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.ProjectionType;
import io.vlingo.xoom.codegen.storage.DatabaseType;
import io.vlingo.xoom.codegen.storage.StorageType;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;

import static io.vlingo.xoom.codegen.CodeGenerationParameter.*;
import static io.vlingo.xoom.codegen.CodeTemplateStandard.*;
import static io.vlingo.xoom.codegen.ProjectionType.EVENT_BASED;
import static io.vlingo.xoom.codegen.ProjectionType.NONE;
import static io.vlingo.xoom.codegen.storage.DatabaseType.HSQLDB;
import static io.vlingo.xoom.codegen.storage.DatabaseType.IN_MEMORY;
import static io.vlingo.xoom.codegen.storage.StorageType.JOURNAL;
import static io.vlingo.xoom.codegen.storage.StorageType.STATE_STORE;

public class StorageGenerationStepTest {

    private static final String HOME_DIRECTORY = OperatingSystem.detect().isWindows() ? "D:\\projects" : "/home";
    private static final String PROJECT_PATH = Paths.get(HOME_DIRECTORY, "xoom-app").toString();
    private static final String MODEL_PACKAGE_PATH =
            Paths.get(PROJECT_PATH, "src", "main", "java",
                    "io", "vlingo", "xoomapp", "model").toString();

    private static final String PERSISTENCE_PACKAGE_PATH =
            Paths.get(PROJECT_PATH, "src", "main", "java",
                    "io", "vlingo", "xoomapp", "infrastructure", "persistence").toString();

    @Test
    public void testJournalGenerationWithHSQLDBDatabase() {
        final CodeGenerationContext context =
                CodeGenerationContext.empty();

        loadProperties(context, JOURNAL, HSQLDB, NONE);
        loadContents(context);

        new StorageGenerationStep().process(context);

        Assert.assertEquals(11, context.contents().size());
        Assert.assertEquals("BookRentedAdapter.java", context.contents().get(7).file.getName());
        Assert.assertEquals("BookPurchasedAdapter.java", context.contents().get(8).file.getName());
        Assert.assertEquals("CommandModelJournalProvider.java", context.contents().get(9).file.getName());
        Assert.assertEquals("QueryModelStateStoreProvider.java", context.contents().get(10).file.getName());

        Assert.assertTrue(context.contents().get(7).text.contains("class BookRentedAdapter implements EntryAdapter<BookRented,TextEntry>"));
        Assert.assertTrue(context.contents().get(8).text.contains("class BookPurchasedAdapter implements EntryAdapter<BookPurchased,TextEntry>"));
        Assert.assertTrue(context.contents().get(9).text.contains("class CommandModelJournalProvider"));
        Assert.assertTrue(context.contents().get(9).text.contains("io.vlingo.symbio.store.journal.jdbc.JDBCJournalActor"));
        Assert.assertFalse(context.contents().get(9).text.contains("InMemoryJournalActor"));
        Assert.assertTrue(context.contents().get(10).text.contains("class QueryModelStateStoreProvider"));
        Assert.assertTrue(context.contents().get(10).text.contains("HSQLDBConfigurationProvider"));
        Assert.assertTrue(context.contents().get(10).text.contains("jdbc:hsqldb:mem:"));
        Assert.assertTrue(context.contents().get(10).text.contains("import io.vlingo.symbio.store.common.jdbc.hsqldb.HSQLDBConfigurationProvider"));
        Assert.assertFalse(context.contents().get(10).text.contains("BookRented"));
        Assert.assertFalse(context.contents().get(10).text.contains("BookPurchased"));
        Assert.assertFalse(context.contents().get(10).text.contains("StatefulTypeRegistry.Info"));
        Assert.assertFalse(context.contents().get(10).text.contains("StateAdapterProvider"));
    }

    @Test
    public void testStateStoreGenerationWithoutProjections() {
        final CodeGenerationContext context =
                CodeGenerationContext.empty();

        loadProperties(context, STATE_STORE, HSQLDB, NONE);
        loadContents(context);

        new StorageGenerationStep().process(context);

        Assert.assertEquals(11, context.contents().size());
        Assert.assertEquals("AuthorStateAdapter.java", context.contents().get(7).file.getName());
        Assert.assertEquals("BookStateAdapter.java", context.contents().get(8).file.getName());
        Assert.assertEquals("CommandModelStateStoreProvider.java", context.contents().get(9).file.getName());
        Assert.assertEquals("QueryModelStateStoreProvider.java", context.contents().get(10).file.getName());

        Assert.assertTrue(context.contents().get(7).text.contains("class AuthorStateAdapter implements StateAdapter<AuthorState,TextState>"));
        Assert.assertTrue(context.contents().get(8).text.contains("class BookStateAdapter implements StateAdapter<BookState,TextState>"));
        Assert.assertTrue(context.contents().get(9).text.contains("class CommandModelStateStoreProvider"));
        Assert.assertTrue(context.contents().get(9).text.contains("import io.vlingo.symbio.store.state.jdbc.JDBCStateStoreActor.JDBCStateStoreInstantiator;"));
        Assert.assertFalse(context.contents().get(9).text.contains("InMemoryStateStoreActor"));
        Assert.assertTrue(context.contents().get(10).text.contains("class QueryModelStateStoreProvider"));
        Assert.assertTrue(context.contents().get(10).text.contains("HSQLDBConfigurationProvider"));
        Assert.assertTrue(context.contents().get(10).text.contains("jdbc:hsqldb:mem:"));
        Assert.assertTrue(context.contents().get(10).text.contains("import io.vlingo.symbio.store.common.jdbc.hsqldb.HSQLDBConfigurationProvider"));
    }

    @Test
    public void testStateStoreGenerationWithProjections() {
        final CodeGenerationContext context =
                CodeGenerationContext.empty();

        loadProperties(context, STATE_STORE, IN_MEMORY, EVENT_BASED);
        loadContents(context);

        new StorageGenerationStep().process(context);

        Assert.assertEquals(11, context.contents().size());
        Assert.assertEquals("AuthorStateAdapter.java", context.contents().get(7).file.getName());
        Assert.assertEquals("BookStateAdapter.java", context.contents().get(8).file.getName());
        Assert.assertEquals("CommandModelStateStoreProvider.java", context.contents().get(9).file.getName());
        Assert.assertEquals("QueryModelStateStoreProvider.java", context.contents().get(10).file.getName());

        Assert.assertTrue(context.contents().get(7).text.contains("class AuthorStateAdapter implements StateAdapter<AuthorState,TextState>"));
        Assert.assertTrue(context.contents().get(8).text.contains("class BookStateAdapter implements StateAdapter<BookState,TextState>"));
        Assert.assertTrue(context.contents().get(9).text.contains("class CommandModelStateStoreProvider"));
        Assert.assertTrue(context.contents().get(10).text.contains("class QueryModelStateStoreProvider"));
    }

    private void loadProperties(final CodeGenerationContext context,
                                final StorageType storageType,
                                final DatabaseType databaseType,
                                final ProjectionType projectionType) {
        context.with(PACKAGE, "io.vlingo").with(APPLICATION_NAME, "xoomapp")
                .with(CQRS, "true").with(DATABASE, databaseType.name())
                .with(COMMAND_MODEL_DATABASE, databaseType.name())
                .with(QUERY_MODEL_DATABASE, databaseType.name())
                .with(STORAGE_TYPE, storageType.name())
                .with(PROJECTIONS, projectionType.name())
                .with(TARGET_FOLDER, HOME_DIRECTORY);
    }

    private void loadContents(final CodeGenerationContext context) {
        context.addContent(STATE, new File(Paths.get(MODEL_PACKAGE_PATH, "author", "AuthorState.java").toString()), AUTHOR_STATE_CONTENT_TEXT);
        context.addContent(STATE, new File(Paths.get(MODEL_PACKAGE_PATH, "book", "BookState.java").toString()), BOOK_STATE_CONTENT_TEXT);
        context.addContent(AGGREGATE_PROTOCOL, new File(Paths.get(MODEL_PACKAGE_PATH, "author", "Author.java").toString()), AUTHOR_CONTENT_TEXT);
        context.addContent(AGGREGATE_PROTOCOL, new File(Paths.get(MODEL_PACKAGE_PATH, "book", "Book.java").toString()), BOOK_CONTENT_TEXT);
        context.addContent(DOMAIN_EVENT, new File(Paths.get(MODEL_PACKAGE_PATH, "book", "BookRented.java").toString()), BOOK_RENTED_TEXT);
        context.addContent(DOMAIN_EVENT, new File(Paths.get(MODEL_PACKAGE_PATH, "book", "BookPurchased.java").toString()), BOOK_PURCHASED_TEXT);
        context.addContent(PROJECTION_DISPATCHER_PROVIDER, new File(Paths.get(PERSISTENCE_PACKAGE_PATH, "ProjectionDispatcherProvider.java").toString()), PROJECTION_DISPATCHER_PROVIDER_CONTENT_TEXT);
    }

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
