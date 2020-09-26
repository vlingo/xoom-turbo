// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.autodispatch;

import io.vlingo.xoom.OperatingSystem;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateFile;
import io.vlingo.xoom.codegen.template.TemplateParameter;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.storage.QueriesTemplateDataFactory;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static io.vlingo.xoom.codegen.template.TemplateParameter.AUTO_DISPATCH_HANDLERS_MAPPING_NAME;
import static io.vlingo.xoom.codegen.template.TemplateParameter.AUTO_DISPATCH_MAPPING_NAME;
import static io.vlingo.xoom.codegen.template.TemplateStandard.*;

public class AutoDispatchMappingTemplateDataFactoryTest {

    @Test
    public void testThatTemplateParametersAreMapped() {
        final String basePackage = "io.vlingo.xoomapp";
        final String restResourcesData = "Author;Book";
        final String persistencePackage = basePackage + ".infrastructure.persistence";

        final List<TemplateData> queriesTemplateData =
                QueriesTemplateDataFactory.from(persistencePackage, true, contents());

        final List<TemplateData> mappingTemplatesData =
                AutoDispatchMappingTemplateDataFactory.build(basePackage, restResourcesData,
                        true, queriesTemplateData, contents());

        Assert.assertEquals(4, mappingTemplatesData.size());

        final TemplateParameters autoDispatchMappingParameters =
                mappingTemplatesData.stream()
                        .filter(data -> data.hasStandard(AUTO_DISPATCH_MAPPING)).map(TemplateData::parameters)
                        .filter(params-> params.find(AUTO_DISPATCH_MAPPING_NAME).equals("AuthorResource"))
                        .findFirst().get();

        Assert.assertTrue(autoDispatchMappingParameters.hasImport("io.vlingo.xoomapp.model.author.Author"));
        Assert.assertTrue(autoDispatchMappingParameters.hasImport("io.vlingo.xoomapp.model.author.AuthorEntity"));
        Assert.assertTrue(autoDispatchMappingParameters.hasImport("io.vlingo.xoomapp.infrastructure.AuthorData"));
        Assert.assertTrue(autoDispatchMappingParameters.hasImport("io.vlingo.xoomapp.infrastructure.persistence.AuthorQueries"));
        Assert.assertTrue(autoDispatchMappingParameters.hasImport("io.vlingo.xoomapp.infrastructure.persistence.AuthorQueriesActor"));
        Assert.assertEquals("io.vlingo.xoomapp.resource", autoDispatchMappingParameters.find(TemplateParameter.PACKAGE_NAME));
        Assert.assertEquals("AuthorResource", autoDispatchMappingParameters.find(TemplateParameter.AUTO_DISPATCH_MAPPING_NAME));
        Assert.assertEquals("Author", autoDispatchMappingParameters.find(TemplateParameter.AGGREGATE_PROTOCOL_NAME));
        Assert.assertEquals("AuthorEntity", autoDispatchMappingParameters.find(TemplateParameter.ENTITY_NAME));
        Assert.assertEquals("AuthorData", autoDispatchMappingParameters.find(TemplateParameter.ENTITY_DATA_NAME));
        Assert.assertEquals("AuthorQueries", autoDispatchMappingParameters.find(TemplateParameter.QUERIES_NAME));
        Assert.assertEquals("AuthorQueriesActor", autoDispatchMappingParameters.find(TemplateParameter.QUERIES_ACTOR_NAME));
        Assert.assertEquals("authors", autoDispatchMappingParameters.find(TemplateParameter.URI_ROOT));
        Assert.assertEquals(true, autoDispatchMappingParameters.find(TemplateParameter.USE_CQRS));

        final TemplateParameters autoDispatchHandlersMappingParameters =
                mappingTemplatesData.stream()
                        .filter(data -> data.hasStandard(AUTO_DISPATCH_HANDLERS_MAPPING)).map(TemplateData::parameters)
                        .filter(params-> params.find(AUTO_DISPATCH_HANDLERS_MAPPING_NAME).equals("AuthorResourceHandlers"))
                        .findFirst().get();

        Assert.assertTrue(autoDispatchHandlersMappingParameters.hasImport("io.vlingo.xoomapp.model.author.Author"));
        Assert.assertTrue(autoDispatchHandlersMappingParameters.hasImport("io.vlingo.xoomapp.model.author.AuthorState"));
        Assert.assertTrue(autoDispatchHandlersMappingParameters.hasImport("io.vlingo.xoomapp.infrastructure.AuthorData"));
        Assert.assertTrue(autoDispatchHandlersMappingParameters.hasImport("io.vlingo.xoomapp.infrastructure.persistence.AuthorQueries"));
        Assert.assertEquals("io.vlingo.xoomapp.resource", autoDispatchHandlersMappingParameters.find(TemplateParameter.PACKAGE_NAME));
        Assert.assertEquals("AuthorResourceHandlers", autoDispatchHandlersMappingParameters.find(AUTO_DISPATCH_HANDLERS_MAPPING_NAME));
        Assert.assertEquals("Author", autoDispatchHandlersMappingParameters.find(TemplateParameter.AGGREGATE_PROTOCOL_NAME));
        Assert.assertEquals("AuthorData", autoDispatchHandlersMappingParameters.find(TemplateParameter.ENTITY_DATA_NAME));
        Assert.assertEquals("AuthorQueries", autoDispatchHandlersMappingParameters.find(TemplateParameter.QUERIES_NAME));
        Assert.assertEquals("authors", autoDispatchHandlersMappingParameters.find(TemplateParameter.QUERY_ALL_METHOD_NAME));
        Assert.assertEquals(true, autoDispatchHandlersMappingParameters.find(TemplateParameter.USE_CQRS));
    }

    private List<Content> contents() {
        return Arrays.asList(
                Content.with(AGGREGATE_PROTOCOL, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "Author.java"), null, null, AUTHOR_CONTENT_TEXT),
                Content.with(AGGREGATE_PROTOCOL, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "Book.java"), null, null, BOOK_CONTENT_TEXT),
                Content.with(AGGREGATE, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "AuthorEntity.java"), null, null, AUTHOR_AGGREGATE_CONTENT_TEXT),
                Content.with(AGGREGATE,  new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookEntity.java"), null, null, BOOK_AGGREGATE_CONTENT_TEXT),
                Content.with(STATE, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "AuthorState.java"), null, null, AUTHOR_STATE_CONTENT_TEXT),
                Content.with(STATE, new TemplateFile(Paths.get(MODEL_PACKAGE_PATH, "book").toString(), "BookState.java"), null, null, BOOK_STATE_CONTENT_TEXT),
                Content.with(ENTITY_DATA, new TemplateFile(Paths.get(INFRASTRUCTURE_PACKAGE_PATH).toString(), "AuthorData.java"), null, null, AUTHOR_DATA_CONTENT_TEXT),
                Content.with(ENTITY_DATA, new TemplateFile(Paths.get(INFRASTRUCTURE_PACKAGE_PATH).toString(), "BookData.java"), null, null, BOOK_DATA_CONTENT_TEXT),
                Content.with(QUERIES, new TemplateFile(Paths.get(PERSISTENCE_PACKAGE_PATH).toString(), "AuthorQueries.java"), null, null, AUTHOR_QUERIES_CONTENT_TEXT),
                Content.with(QUERIES, new TemplateFile(Paths.get(PERSISTENCE_PACKAGE_PATH).toString(), "BookQueries.java"), null, null, BOOK_QUERIES_CONTENT_TEXT),
                Content.with(QUERIES_ACTOR, new TemplateFile(Paths.get(PERSISTENCE_PACKAGE_PATH).toString(), "AuthorQueriesActor.java"), null, null, AUTHOR_QUERIES_ACTOR_CONTENT_TEXT),
                Content.with(QUERIES_ACTOR, new TemplateFile(Paths.get(PERSISTENCE_PACKAGE_PATH).toString(), "BookQueriesActor.java"), null, null, BOOK_QUERIES_ACTOR_CONTENT_TEXT)
        );
    }

    private static final String PROJECT_PATH =
            OperatingSystem.detect().isWindows() ?
                    Paths.get("D:\\projects", "xoom-app").toString() :
                    Paths.get("/home", "xoom-app").toString();

    private static final String MODEL_PACKAGE_PATH =
            Paths.get(PROJECT_PATH, "src", "main", "java",
                    "io", "vlingo", "xoomapp", "model").toString();

    private static final String INFRASTRUCTURE_PACKAGE_PATH =
            Paths.get(PROJECT_PATH, "src", "main", "java",
                    "io", "vlingo", "xoomapp", "infrastructure").toString();

    private static final String PERSISTENCE_PACKAGE_PATH =
            Paths.get(INFRASTRUCTURE_PACKAGE_PATH, "persistence").toString();

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

    private static final String AUTHOR_AGGREGATE_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.author; \\n" +
                    "public class AuthorEntity { \\n" +
                    "... \\n" +
                    "}";

    private static final String BOOK_AGGREGATE_CONTENT_TEXT =
            "package io.vlingo.xoomapp.model.book; \\n" +
                    "public class BookEntity { \\n" +
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

    private static final String AUTHOR_QUERIES_CONTENT_TEXT =
            "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                    "public interface AuthorQueries { \\n" +
                    "... \\n" +
                    "}";

    private static final String BOOK_QUERIES_CONTENT_TEXT =
            "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                    "public interface BookQueries { \\n" +
                    "... \\n" +
                    "}";

    private static final String AUTHOR_QUERIES_ACTOR_CONTENT_TEXT =
            "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                    "public class AuthorQueriesActor { \\n" +
                    "... \\n" +
                    "}";

    private static final String BOOK_QUERIES_ACTOR_CONTENT_TEXT =
            "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                    "public class BookQueriesActor { \\n" +
                    "... \\n" +
                    "}";

}
