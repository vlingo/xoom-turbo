// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.projections;

import io.vlingo.xoom.OperatingSystem;
import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.template.TemplateFile;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;

import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.DOMAIN_EVENT;
import static io.vlingo.xoom.codegen.template.TemplateStandard.*;

public class ProjectionGenerationStepTest {

    private static final String HOME_DIRECTORY = OperatingSystem.detect().isWindows() ? "D:\\projects" : "/home";
    private static final String PROJECT_PATH = Paths.get(HOME_DIRECTORY, "xoom-app").toString();
    private static final String MODEL_PACKAGE_PATH =
            Paths.get(PROJECT_PATH, "src", "main", "java",
                    "io", "vlingo", "xoomapp", "model").toString();

    private static final String INFRASTRUCTURE_PACKAGE_PATH =
            Paths.get(PROJECT_PATH, "src", "main", "java",
                    "io", "vlingo", "xoomapp", "infrastructure").toString();

    @Test
    public void testEventBasedProjectionGeneration() {
        final CodeGenerationContext context =
                CodeGenerationContext.empty();

        loadParameters(context, ProjectionType.EVENT_BASED.name());
        loadContents(context);
        new ProjectionGenerationStep().process(context);
        performAssertion(context);
    }

    @Test
    public void testOperationBasedProjectionGeneration() {
        final CodeGenerationContext context =
                CodeGenerationContext.empty();

        loadParameters(context, ProjectionType.OPERATION_BASED.name());
        loadContents(context);
        new ProjectionGenerationStep().process(context);
        performAssertion(context);
    }

    private void performAssertion(final CodeGenerationContext context) {
        final ProjectionType projectionType = context.parameterOf(PROJECTION_TYPE, ProjectionType::valueOf);
        final int contentSize = projectionType.isEventBased() ? 13 : 12;

        Assert.assertEquals(contentSize, context.contents().size());
        if(projectionType.isEventBased()) {
            Assert.assertEquals("EventTypes", context.contents().get(contentSize - 4).retrieveClassName());
        }
        Assert.assertEquals("ProjectionDispatcherProvider", context.contents().get(contentSize - 3).retrieveClassName());
        Assert.assertEquals("BookProjectionActor", context.contents().get(contentSize - 2).retrieveClassName());
        Assert.assertEquals("AuthorProjectionActor", context.contents().get(contentSize - 1).retrieveClassName());

        Assert.assertTrue(context.contents().get(contentSize - 3).contains("class ProjectionDispatcherProvider"));

        Assert.assertTrue(context.contents().get(contentSize - 2).contains("class BookProjectionActor extends StateStoreProjectionActor<BookData>"));
        Assert.assertTrue(context.contents().get(contentSize - 2).contains(projectionType.isEventBased() ? "case BookSoldOut:" : "CreationCase1"));
        Assert.assertTrue(context.contents().get(contentSize - 2).contains(projectionType.isEventBased() ? "BookData.empty()" : "merged = currentData;"));
        Assert.assertTrue(context.contents().get(contentSize - 2).contains(projectionType.isEventBased() ? "case BookPurchased:" : "ChangeCase2"));
        Assert.assertTrue(context.contents().get(contentSize - 1).contains("class AuthorProjectionActor extends StateStoreProjectionActor<AuthorData>"));
        Assert.assertTrue(context.contents().get(contentSize - 1).contains(projectionType.isEventBased() ? "case AuthorRated:" : "CreationCase1"));
    }

    private void loadContents(final CodeGenerationContext context) {
        context.addContent(AGGREGATE_STATE, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "AuthorState.java"), AUTHOR_STATE_CONTENT_TEXT);
        context.addContent(AGGREGATE_STATE, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookState.java"), BOOK_STATE_CONTENT_TEXT);
        context.addContent(AGGREGATE_PROTOCOL, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "Author.java"), AUTHOR_CONTENT_TEXT);
        context.addContent(DOMAIN_EVENT, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "AuthorRated.java"), AUTHOR_RATED_CONTENT_TEXT);
        context.addContent(AGGREGATE_PROTOCOL, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "Book.java"), BOOK_CONTENT_TEXT);
        context.addContent(DOMAIN_EVENT, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookSoldOut.java"), BOOK_SOLD_OUT_CONTENT_TEXT);
        context.addContent(DOMAIN_EVENT, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookPurchased.java"), BOOK_PURCHASED_CONTENT_TEXT);
        context.addContent(DATA_OBJECT, new TemplateFile(Paths.get(INFRASTRUCTURE_PACKAGE_PATH).toString(), "AuthorData.java"), AUTHOR_DATA_CONTENT_TEXT);
        context.addContent(DATA_OBJECT, new TemplateFile(Paths.get(INFRASTRUCTURE_PACKAGE_PATH).toString(), "BookData.java"), BOOK_DATA_CONTENT_TEXT);
    }

    private void loadParameters(final CodeGenerationContext context, final String projections) {
        context.with(PACKAGE, "io.vlingo").with(APPLICATION_NAME, "xoomapp")
                .with(STORAGE_TYPE, "STATE_STORE").with(PROJECTION_TYPE, projections)
                .with(TARGET_FOLDER, HOME_DIRECTORY);
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

    private static final String BOOK_SOLD_OUT_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.book; \\n" +
                    "public class BookSoldOut extends DomainEvent { \\n" +
                    "... \\n" +
                    "}";

    private static final String BOOK_PURCHASED_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.book; \\n" +
                    "public class BookPurchased extends DomainEvent { \\n" +
                    "... \\n" +
                    "}";

    private static final String AUTHOR_RATED_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.author; \\n" +
                    "public class AuthorRated extends DomainEvent { \\n" +
                    "... \\n" +
                    "}";

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
}