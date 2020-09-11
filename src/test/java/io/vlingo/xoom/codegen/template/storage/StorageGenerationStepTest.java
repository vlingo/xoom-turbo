// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.storage;

import io.vlingo.xoom.OperatingSystem;
import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.template.TemplateFile;
import io.vlingo.xoom.codegen.template.projections.ProjectionType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;

import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.*;
import static io.vlingo.xoom.codegen.template.projections.ProjectionType.EVENT_BASED;
import static io.vlingo.xoom.codegen.template.projections.ProjectionType.NONE;
import static io.vlingo.xoom.codegen.template.storage.DatabaseType.HSQLDB;
import static io.vlingo.xoom.codegen.template.storage.DatabaseType.IN_MEMORY;
import static io.vlingo.xoom.codegen.template.storage.StorageType.JOURNAL;
import static io.vlingo.xoom.codegen.template.storage.StorageType.STATE_STORE;

public class StorageGenerationStepTest {

    @Test
    public void testJournalGenerationWithHSQLDBDatabase() {
        final CodeGenerationContext context =
                CodeGenerationContext.empty();

        loadProperties(context, JOURNAL, HSQLDB, NONE);
        loadContents(context);

        new StorageGenerationStep().process(context);

        Assert.assertEquals(18, context.contents().size());
        Assert.assertEquals("BookRentedAdapter", context.contents().get(9).retrieveClassName());
        Assert.assertEquals("BookPurchasedAdapter", context.contents().get(10).retrieveClassName());
        Assert.assertEquals("CommandModelJournalProvider", context.contents().get(16).retrieveClassName());
        Assert.assertEquals("QueryModelStateStoreProvider", context.contents().get(17).retrieveClassName());

        Assert.assertTrue(context.contents().get(9).contains("class BookRentedAdapter implements EntryAdapter<BookRented,TextEntry>"));
        Assert.assertTrue(context.contents().get(10).contains("class BookPurchasedAdapter implements EntryAdapter<BookPurchased,TextEntry>"));
        Assert.assertTrue(context.contents().get(15).contains("database.driver=org.hsqldb.jdbc.JDBCDriver"));
        Assert.assertTrue(context.contents().get(15).contains("database.url=jdbc:hsqldb:mem:"));
        Assert.assertTrue(context.contents().get(15).contains("query.database.driver=org.hsqldb.jdbc.JDBCDriver"));
        Assert.assertTrue(context.contents().get(15).contains("query.database.url=jdbc:hsqldb:mem:"));
        Assert.assertTrue(context.contents().get(16).contains("class CommandModelJournalProvider"));
        Assert.assertFalse(context.contents().get(16).contains("public final BookQueries bookQueries;"));
        Assert.assertTrue(context.contents().get(16).contains("StoreActorBuilder.from(stage, Model.COMMAND, dispatcher, StorageType.JOURNAL, Settings.properties(), true"));
        Assert.assertTrue(context.contents().get(17).contains("class QueryModelStateStoreProvider"));
        Assert.assertTrue(context.contents().get(17).contains("public final BookQueries bookQueries;"));
        Assert.assertTrue(context.contents().get(17).contains("this.bookQueries = stage.actorFor(BookQueries.class, BookQueriesActor.class, store)"));
        Assert.assertTrue(context.contents().get(17).contains("StoreActorBuilder.from(stage, Model.QUERY, dispatcher, StorageType.STATE_STORE, Settings.properties(), true"));
        Assert.assertFalse(context.contents().get(17).contains("BookRented"));
        Assert.assertFalse(context.contents().get(17).contains("BookPurchased"));
        Assert.assertFalse(context.contents().get(17).contains("StatefulTypeRegistry.Info"));
        Assert.assertFalse(context.contents().get(17).contains("StateAdapterProvider"));
    }

    @Test
    public void testStateStoreGenerationWithoutProjections() {
        final CodeGenerationContext context =
                CodeGenerationContext.empty();

        loadProperties(context, STATE_STORE, HSQLDB, NONE);
        loadContents(context);

        new StorageGenerationStep().process(context);

        Assert.assertEquals(18, context.contents().size());
        Assert.assertEquals("BookStateAdapter", context.contents().get(9).retrieveClassName());
        Assert.assertEquals("AuthorStateAdapter", context.contents().get(10).retrieveClassName());
        Assert.assertEquals("CommandModelStateStoreProvider", context.contents().get(16).retrieveClassName());
        Assert.assertEquals("QueryModelStateStoreProvider", context.contents().get(17).retrieveClassName());

        Assert.assertTrue(context.contents().get(9).contains("class BookStateAdapter implements StateAdapter<BookState,TextState>"));
        Assert.assertTrue(context.contents().get(10).contains("class AuthorStateAdapter implements StateAdapter<AuthorState,TextState>"));
        Assert.assertTrue(context.contents().get(16).contains("class CommandModelStateStoreProvider"));
        Assert.assertTrue(context.contents().get(16).contains("StoreActorBuilder.from(stage, Model.COMMAND, dispatcher, StorageType.STATE_STORE, Settings.properties(), true"));
        Assert.assertTrue(context.contents().get(17).contains("class QueryModelStateStoreProvider"));
        Assert.assertTrue(context.contents().get(17).contains("StoreActorBuilder.from(stage, Model.QUERY, dispatcher, StorageType.STATE_STORE, Settings.properties(), true"));
    }

    @Test
    public void testStateStoreGenerationWithProjections() {
        final CodeGenerationContext context =
                CodeGenerationContext.empty();

        loadProperties(context, STATE_STORE, IN_MEMORY, EVENT_BASED);
        loadContents(context);

        new StorageGenerationStep().process(context);

        Assert.assertEquals(18, context.contents().size());
        Assert.assertEquals("BookStateAdapter", context.contents().get(9).retrieveClassName());
        Assert.assertEquals("AuthorStateAdapter", context.contents().get(10).retrieveClassName());
        Assert.assertEquals("CommandModelStateStoreProvider", context.contents().get(16).retrieveClassName());
        Assert.assertEquals("QueryModelStateStoreProvider", context.contents().get(17).retrieveClassName());

        Assert.assertTrue(context.contents().get(9).contains("class BookStateAdapter implements StateAdapter<BookState,TextState>"));
        Assert.assertTrue(context.contents().get(10).contains("class AuthorStateAdapter implements StateAdapter<AuthorState,TextState>"));
        Assert.assertTrue(context.contents().get(16).contains("class CommandModelStateStoreProvider"));
        Assert.assertTrue(context.contents().get(17).contains("class QueryModelStateStoreProvider"));
    }

    @Test
    public void testAnnotatedStoreGeneration() {
        final CodeGenerationContext context =
                CodeGenerationContext.empty().with(ANNOTATIONS, "true");

        loadProperties(context, STATE_STORE, IN_MEMORY, EVENT_BASED);
        loadContents(context);

        new StorageGenerationStep().process(context);

        Assert.assertEquals(17, context.contents().size());
        Assert.assertEquals("BookStateAdapter", context.contents().get(9).retrieveClassName());
        Assert.assertEquals("AuthorStateAdapter", context.contents().get(10).retrieveClassName());
        Assert.assertEquals("PersistenceSetup", context.contents().get(16).retrieveClassName());

        Assert.assertTrue(context.contents().get(9).contains("class BookStateAdapter implements StateAdapter<BookState,TextState>"));
        Assert.assertTrue(context.contents().get(10).contains("class AuthorStateAdapter implements StateAdapter<AuthorState,TextState>"));
        Assert.assertTrue(context.contents().get(16).contains("class PersistenceSetup"));
        Assert.assertTrue(context.contents().get(16).contains("@Persistence(basePackage = \"io.vlingo\", storageType = StorageType.STATE_STORE, cqrs = true)"));
        Assert.assertTrue(context.contents().get(16).contains("@Projections({"));
        Assert.assertTrue(context.contents().get(16).contains("@Projection(actor = AuthorProjectionActor.class, becauseOf = {}),"));
        Assert.assertTrue(context.contents().get(16).contains("@Projection(actor = BookProjectionActor.class, becauseOf = {BookRented.class, BookPurchased.class})"));
        Assert.assertTrue(context.contents().get(16).contains("@Adapters({"));
        Assert.assertTrue(context.contents().get(16).contains("BookState.class,"));
        Assert.assertTrue(context.contents().get(16).contains("AuthorState.class"));
        Assert.assertTrue(!context.contents().get(16).contains("AuthorState.class,"));
        Assert.assertTrue(context.contents().get(16).contains("import io.vlingo.xoom.annotation.persistence.EnableQueries;"));
        Assert.assertTrue(context.contents().get(16).contains("import io.vlingo.xoom.annotation.persistence.QueriesEntry;"));
        Assert.assertTrue(context.contents().get(16).contains("@EnableQueries({"));
        Assert.assertTrue(context.contents().get(16).contains("@QueriesEntry(protocol = AuthorQueries.class, actor = AuthorQueriesActor.class)"));
        Assert.assertTrue(context.contents().get(16).contains("@QueriesEntry(protocol = BookQueries.class, actor = BookQueriesActor.class)"));
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
                .with(PROJECTION_TYPE, projectionType.name())
                .with(TARGET_FOLDER, HOME_DIRECTORY);
    }

    private void loadContents(final CodeGenerationContext context) {
        context.addContent(STATE, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "AuthorState.java"), AUTHOR_STATE_CONTENT_TEXT);
        context.addContent(STATE, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookState.java"), BOOK_STATE_CONTENT_TEXT);
        context.addContent(AGGREGATE_PROTOCOL, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "Author.java"), AUTHOR_CONTENT_TEXT);
        context.addContent(AGGREGATE_PROTOCOL, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "Book.java"), BOOK_CONTENT_TEXT);
        context.addContent(DOMAIN_EVENT, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookRented.java"), BOOK_RENTED_TEXT);
        context.addContent(DOMAIN_EVENT, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookPurchased.java"), BOOK_PURCHASED_TEXT);
        context.addContent(PROJECTION_DISPATCHER_PROVIDER, new TemplateFile(PERSISTENCE_PACKAGE_PATH, "ProjectionDispatcherProvider.java"), PROJECTION_DISPATCHER_PROVIDER_CONTENT_TEXT);
        context.addContent(ENTITY_DATA, new TemplateFile(Paths.get(INFRASTRUCTURE_PACKAGE_PATH).toString(), "AuthorData.java"), AUTHOR_DATA_CONTENT_TEXT);
        context.addContent(ENTITY_DATA, new TemplateFile(Paths.get(INFRASTRUCTURE_PACKAGE_PATH).toString(), "BookData.java"), BOOK_DATA_CONTENT_TEXT);
    }

    private static final String HOME_DIRECTORY = OperatingSystem.detect().isWindows() ? "D:\\projects" : "/home";
    private static final String PROJECT_PATH = Paths.get(HOME_DIRECTORY, "xoom-app").toString();
    private static final String MODEL_PACKAGE_PATH =
            Paths.get(PROJECT_PATH, "src", "main", "java",
                    "io", "vlingo", "xoomapp", "model").toString();

    private static final String PERSISTENCE_PACKAGE_PATH =
            Paths.get(PROJECT_PATH, "src", "main", "java",
                    "io", "vlingo", "xoomapp", "infrastructure", "persistence").toString();

    private static final String INFRASTRUCTURE_PACKAGE_PATH =
            Paths.get(PROJECT_PATH, "src", "main", "java",
                    "io", "vlingo", "xoomapp", "infrastructure").toString();

    private static final String  AUTHOR_DATA_CONTENT_TEXT =
            "package io.vlingo.xoomapp.infrastructure; \\n" +
                    "public class AuthorData { \\n" +
                    "... \\n" +
                    "}";

    private static final String BOOK_DATA_CONTENT_TEXT =
            "package io.vlingo.xoomapp.infrastructure; \\n" +
                    "public class BookData { \\n" +
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
