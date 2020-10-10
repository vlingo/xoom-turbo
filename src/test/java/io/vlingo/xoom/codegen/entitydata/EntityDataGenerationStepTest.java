// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.entitydata;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameters;
import io.vlingo.xoom.codegen.template.TemplateFile;
import io.vlingo.xoom.codegen.template.entitydata.EntityDataGenerationStep;
import org.junit.Assert;
import org.junit.Test;

import static io.vlingo.xoom.codegen.parameter.Label.PACKAGE;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE_PROTOCOL;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE_STATE;

public class EntityDataGenerationStepTest {

    @Test
    public void testThatEntitiesDataAreGenerated() {
        final CodeGenerationParameters parameters =
                CodeGenerationParameters.from(PACKAGE, "io.vlingo.xoomapp");

        final CodeGenerationContext context =
                CodeGenerationContext.with(parameters)
                        .contents(contents());

        new EntityDataGenerationStep().process(context);

        Assert.assertEquals(6, context.contents().size());

        final Content bookDataContent =
                context.contents().stream()
                        .filter(content -> content.retrieveClassName().equals("BookData"))
                        .findFirst().get();

        Assert.assertTrue(bookDataContent.contains("public class BookData"));
        Assert.assertTrue(bookDataContent.contains("final BookState state"));
        Assert.assertTrue(bookDataContent.contains("import io.vlingo.xoomapp.model.book.BookState;"));
    }

    private Content[] contents() {
        return new Content[]{
                Content.with(AGGREGATE_STATE, new TemplateFile("/Projects/", "AuthorState.java"), null, null, AUTHOR_STATE_CONTENT_TEXT),
                Content.with(AGGREGATE_STATE, new TemplateFile("/Projects/", "BookState.java"), null, null, BOOK_STATE_CONTENT_TEXT),
                Content.with(AGGREGATE_PROTOCOL, new TemplateFile("/Projects/", "Author.java"), null, null, AUTHOR_PROTOCOL_CONTENT_TEXT),
                Content.with(AGGREGATE_PROTOCOL, new TemplateFile("/Projects/", "Book.java"), null, null, BOOK_PROTOCOL_CONTENT_TEXT)
        };
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

    private static final String AUTHOR_PROTOCOL_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.author; \\n" +
                    "public class Author { \\n" +
                    "... \\n" +
                    "}";

    private static final String BOOK_PROTOCOL_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.book; \\n" +
                    "public class Book { \\n" +
                    "... \\n" +
                    "}";

}
