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
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;

import static io.vlingo.xoom.codegen.CodeGenerationParameter.*;
import static io.vlingo.xoom.codegen.CodeTemplateStandard.AGGREGATE_PROTOCOL;
import static io.vlingo.xoom.codegen.CodeTemplateStandard.STATE;

public class ProjectionGenerationStepTest {

    private static final String HOME_DIRECTORY = OperatingSystem.detect().isWindows() ? "D:\\projects" : "/home";
    private static final String PROJECT_PATH = Paths.get(HOME_DIRECTORY, "xoom-app").toString();
    private static final String MODEL_PACKAGE_PATH =
            Paths.get(PROJECT_PATH, "src", "main", "java",
                    "io", "vlingo", "xoomapp", "model").toString();

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
        final ProjectionType projectionType = context.propertyOf(PROJECTIONS, ProjectionType::valueOf);
        final String expectedProjectionComment = projectionType.isEventBased() ? "replace with event" : "replace with operation text";

        Assert.assertEquals(9, context.contents().size());
        Assert.assertEquals("ProjectionDispatcherProvider.java", context.contents().get(4).file.getName());
        Assert.assertEquals("AuthorData.java", context.contents().get(5).file.getName());
        Assert.assertEquals("AuthorProjectionActor.java", context.contents().get(6).file.getName());
        Assert.assertEquals("BookData.java", context.contents().get(7).file.getName());
        Assert.assertEquals("BookProjectionActor.java", context.contents().get(8).file.getName());

        Assert.assertTrue(context.contents().get(4).text.contains("class ProjectionDispatcherProvider"));
        Assert.assertTrue(context.contents().get(5).text.contains("class AuthorData"));
        Assert.assertTrue(context.contents().get(6).text.contains("class AuthorProjectionActor extends StateStoreProjectionActor<AuthorData>"));
        Assert.assertTrue(context.contents().get(6).text.contains(expectedProjectionComment));
        Assert.assertTrue(context.contents().get(7).text.contains("class BookData"));
        Assert.assertTrue(context.contents().get(8).text.contains("class BookProjectionActor extends StateStoreProjectionActor<BookData>"));
        Assert.assertTrue(context.contents().get(8).text.contains(expectedProjectionComment));
    }

    private void loadContents(final CodeGenerationContext context) {
        context.addContent(STATE, new File(Paths.get(MODEL_PACKAGE_PATH, "author", "AuthorState.java").toString()), AUTHOR_STATE_CONTENT_TEXT);
        context.addContent(STATE, new File(Paths.get(MODEL_PACKAGE_PATH, "book", "BookState.java").toString()), BOOK_STATE_CONTENT_TEXT);
        context.addContent(AGGREGATE_PROTOCOL, new File(Paths.get(MODEL_PACKAGE_PATH, "author", "Author.java").toString()), AUTHOR_CONTENT_TEXT);
        context.addContent(AGGREGATE_PROTOCOL, new File(Paths.get(MODEL_PACKAGE_PATH, "book", "Book.java").toString()), BOOK_CONTENT_TEXT);
    }

    private void loadParameters(final CodeGenerationContext context, final String projections) {
        context.with(PACKAGE, "io.vlingo").with(APPLICATION_NAME, "xoomapp")
                .with(STORAGE_TYPE, "STATE_STORE").with(PROJECTIONS, projections)
                .with(TARGET_FOLDER, HOME_DIRECTORY);
    }

    private static final String AUTHOR_STATE_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model; \\n" +
                    "public class AuthorState { \\n" +
                    "... \\n" +
                    "}";

    private static final String BOOK_STATE_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model; \\n" +
                    "public class BookState { \\n" +
                    "... \\n" +
                    "}";

    private static final String AUTHOR_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model; \\n" +
                    "public interface Author { \\n" +
                    "... \\n" +
                    "}";

    private static final String BOOK_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model; \\n" +
                    "public interface Book { \\n" +
                    "... \\n" +
                    "}";

}