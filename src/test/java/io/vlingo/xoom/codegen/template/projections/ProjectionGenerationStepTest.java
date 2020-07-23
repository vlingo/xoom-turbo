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

import static io.vlingo.xoom.codegen.CodeGenerationParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE_PROTOCOL;
import static io.vlingo.xoom.codegen.template.TemplateStandard.STATE;

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
        final ProjectionType projectionType = context.parameterOf(PROJECTIONS, ProjectionType::valueOf);
        final String expectedProjectionComment = projectionType.isEventBased() ? "replace with event" : "replace with operation text";

        Assert.assertEquals(9, context.contents().size());
        Assert.assertEquals("ProjectionDispatcherProvider", context.contents().get(4).retrieveClassName());
        Assert.assertEquals("AuthorData", context.contents().get(5).retrieveClassName());
        Assert.assertEquals("AuthorProjectionActor", context.contents().get(6).retrieveClassName());
        Assert.assertEquals("BookData", context.contents().get(7).retrieveClassName());
        Assert.assertEquals("BookProjectionActor", context.contents().get(8).retrieveClassName());

        Assert.assertTrue(context.contents().get(4).contains("class ProjectionDispatcherProvider"));
        Assert.assertTrue(context.contents().get(5).contains("class AuthorData"));
        Assert.assertTrue(context.contents().get(6).contains("class AuthorProjectionActor extends StateStoreProjectionActor<AuthorData>"));
        Assert.assertTrue(context.contents().get(6).contains(expectedProjectionComment));
        Assert.assertTrue(context.contents().get(7).contains("class BookData"));
        Assert.assertTrue(context.contents().get(8).contains("class BookProjectionActor extends StateStoreProjectionActor<BookData>"));
        Assert.assertTrue(context.contents().get(8).contains(expectedProjectionComment));
    }

    private void loadContents(final CodeGenerationContext context) {
        context.addContent(STATE, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "AuthorState.java"), AUTHOR_STATE_CONTENT_TEXT);
        context.addContent(STATE, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookState.java"), BOOK_STATE_CONTENT_TEXT);
        context.addContent(AGGREGATE_PROTOCOL, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "Author.java"), AUTHOR_CONTENT_TEXT);
        context.addContent(AGGREGATE_PROTOCOL, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "Book.java"), BOOK_CONTENT_TEXT);
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