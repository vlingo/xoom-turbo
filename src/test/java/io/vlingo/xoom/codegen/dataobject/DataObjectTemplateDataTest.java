// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.dataobject;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateFile;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.dataobject.DataObjectTemplateData;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE_PROTOCOL;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE_STATE;

public class DataObjectTemplateDataTest {

    @Test
    public void testThatTemplateParametersAreMapped() {
        final List<TemplateData> templatesData =
                DataObjectTemplateData.from("io.vlingo.xoomapp",
                        Stream.of(authorAggregate(), bookAggregate()),
                        contents());

        Assert.assertEquals(2, templatesData.size());

        final TemplateParameters authorDataParameters =
                templatesData.stream().filter(data -> data.filename().equals("AuthorData"))
                        .map(TemplateData::parameters).findFirst().get();

        Assert.assertEquals("0", authorDataParameters.find(DEFAULT_ID));
        Assert.assertEquals("AuthorData", authorDataParameters.find(DATA_OBJECT_NAME));
        Assert.assertEquals("AuthorState", authorDataParameters.find(STATE_NAME));
        Assert.assertEquals("io.vlingo.xoomapp.infrastructure", authorDataParameters.find(PACKAGE_NAME));
        Assert.assertEquals("public final long id;", authorDataParameters.<List<String>>find(MEMBERS).get(0));
        Assert.assertEquals("public final String name;", authorDataParameters.<List<String>>find(MEMBERS).get(1));
        Assert.assertEquals("public final int rank;", authorDataParameters.<List<String>>find(MEMBERS).get(2));
        Assert.assertEquals("public final boolean status;", authorDataParameters.<List<String>>find(MEMBERS).get(3));
        Assert.assertEquals("this.id = state.id;", authorDataParameters.<List<String>>find(MEMBERS_ASSIGNMENT).get(0));
        Assert.assertEquals("this.name = state.name;", authorDataParameters.<List<String>>find(MEMBERS_ASSIGNMENT).get(1));
        Assert.assertEquals("this.rank = state.rank;", authorDataParameters.<List<String>>find(MEMBERS_ASSIGNMENT).get(2));
        Assert.assertEquals("this.status = state.status;", authorDataParameters.<List<String>>find(MEMBERS_ASSIGNMENT).get(3));
        Assert.assertEquals("final long id, final String name, final int rank, final boolean status", authorDataParameters.find(CONSTRUCTOR_PARAMETERS));
        Assert.assertEquals("io.vlingo.xoomapp.infrastructure.AuthorData", authorDataParameters.find(DATA_OBJECT_QUALIFIED_NAME));
        Assert.assertEquals("io.vlingo.xoomapp.model.author.AuthorState", authorDataParameters.find(STATE_QUALIFIED_CLASS_NAME));
    }

    private CodeGenerationParameter authorAggregate() {
        final CodeGenerationParameter idField =
                CodeGenerationParameter.of(Label.STATE_FIELD, "id")
                        .relate(Label.FIELD_TYPE, "long");

        final CodeGenerationParameter nameField =
                CodeGenerationParameter.of(Label.STATE_FIELD, "name")
                        .relate(Label.FIELD_TYPE, "String");

        final CodeGenerationParameter rankField =
                CodeGenerationParameter.of(Label.STATE_FIELD, "rank")
                        .relate(Label.FIELD_TYPE, "int");

        final CodeGenerationParameter statusField =
                CodeGenerationParameter.of(Label.STATE_FIELD, "status")
                        .relate(Label.FIELD_TYPE, "boolean");

        return CodeGenerationParameter.of(Label.AGGREGATE, "Author")
                .relate(idField).relate(nameField).relate(rankField).relate(statusField);
    }

    private CodeGenerationParameter bookAggregate() {
        final CodeGenerationParameter idField =
                CodeGenerationParameter.of(Label.STATE_FIELD, "id")
                        .relate(Label.FIELD_TYPE, "long");

        final CodeGenerationParameter nameField =
                CodeGenerationParameter.of(Label.STATE_FIELD, "title")
                        .relate(Label.FIELD_TYPE, "String");

        final CodeGenerationParameter rankField =
                CodeGenerationParameter.of(Label.STATE_FIELD, "publisher")
                        .relate(Label.FIELD_TYPE, "String");

        return CodeGenerationParameter.of(Label.AGGREGATE, "Book")
                .relate(idField).relate(nameField).relate(rankField);
    }

    private List<Content> contents() {
        return Arrays.asList(
                Content.with(AGGREGATE_STATE, new TemplateFile("/Projects/", "AuthorState.java"), null, null, AUTHOR_STATE_CONTENT_TEXT),
                Content.with(AGGREGATE_STATE, new TemplateFile("/Projects/", "BookState.java"), null, null, BOOK_STATE_CONTENT_TEXT),
                Content.with(AGGREGATE_PROTOCOL, new TemplateFile("/Projects/", "Author.java"), null, null, AUTHOR_PROTOCOL_CONTENT_TEXT),
                Content.with(AGGREGATE_PROTOCOL, new TemplateFile("/Projects/", "Book.java"), null, null, BOOK_PROTOCOL_CONTENT_TEXT)
        );
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
