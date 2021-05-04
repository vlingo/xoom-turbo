// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.codegen.content;

import io.vlingo.xoom.turbo.codegen.template.OutputFile;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static io.vlingo.xoom.turbo.codegen.template.DesignerTemplateStandard.AGGREGATE;
import static io.vlingo.xoom.turbo.codegen.template.DesignerTemplateStandard.AGGREGATE_STATE;

public class ContentQueryTest {

    private static final String AUTHOR_STATE_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model; \\n" +
            "import io.vlingo.xoom.symbio.store.object.StateObject; \\n" +
            "public class AuthorState { \\n" +
            "... \\n" +
            "}";

    private static final String BOOK_STATE_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model; \\n" +
                    "import io.vlingo.xoom.symbio.store.object.StateObject; \\n" +
                    "public class BookState { \\n" +
                    "... \\n" +
                    "}";

    private static final String AGGREGATE_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model; \\n" +
            "public class Author { \\n" +
            "... \\n" +
             "}";

    @Test
    public void testClassNameQuery() {
        final Set<String> classNames =
                ContentQuery.findClassNames(AGGREGATE_STATE, contents());

        Assert.assertEquals(2, classNames.size());
        Assert.assertTrue(classNames.contains("AuthorState"));
        Assert.assertTrue(classNames.contains("BookState"));
    }

    @Test
    public void testQualifiedClassNameQuery() {
        final Set<String> classNames =
                ContentQuery.findFullyQualifiedClassNames(AGGREGATE_STATE, contents());

        Assert.assertEquals(2, classNames.size());
        Assert.assertTrue(classNames.contains("io.vlingo.xoomapp.model.AuthorState"));
        Assert.assertTrue(classNames.contains("io.vlingo.xoomapp.model.BookState"));
    }

    private List<Content> contents() {
        return Arrays.asList(
            Content.with(AGGREGATE_STATE, new OutputFile("/Projects/", "AuthorState.java"), null, null, AUTHOR_STATE_CONTENT_TEXT),
            Content.with(AGGREGATE_STATE, new OutputFile("/Projects/", "BookState.java"), null, null, BOOK_STATE_CONTENT_TEXT),
            Content.with(AGGREGATE, new OutputFile("/Projects/", "Author.java"), null, null, AGGREGATE_CONTENT_TEXT)
        );
    }

}
