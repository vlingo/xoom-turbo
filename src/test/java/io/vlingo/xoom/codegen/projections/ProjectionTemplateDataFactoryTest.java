// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.projections;

import io.vlingo.xoom.OperatingSystem;
import io.vlingo.xoom.codegen.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.vlingo.xoom.codegen.CodeTemplateParameter.*;
import static io.vlingo.xoom.codegen.CodeTemplateStandard.*;

public class ProjectionTemplateDataFactoryTest {

    @Test
    public void testEventBasedProjectionTemplateDataBuild() {
        final List<TemplateData> allTemplatesData =
                ProjectionTemplateDataFactory.build("io.vlingo.xoomapp", PROJECT_PATH,
                        ProjectionType.EVENT_BASED, contents());

        //General Assert

        Assert.assertEquals(5, allTemplatesData.size());
        Assert.assertEquals(1, allTemplatesData.stream().filter(templateData -> templateData.standard().equals(PROJECTION_DISPATCHER_PROVIDER)).count());
        Assert.assertEquals(2, allTemplatesData.stream().filter(templateData -> templateData.standard().equals(PROJECTION)).count());
        Assert.assertEquals(2, allTemplatesData.stream().filter(templateData -> templateData.standard().equals(ENTITY_DATA)).count());

        //Assert for ProjectionDispatcherProvider

        final TemplateData providerTemplateData = allTemplatesData.stream().filter(templateData -> templateData.standard().equals(PROJECTION_DISPATCHER_PROVIDER)).findFirst().get();
        final CodeTemplateParameters providerTemplateDataParameters = providerTemplateData.parameters();

        Assert.assertEquals(EXPECTED_PERSISTENCE_PACKAGE, providerTemplateDataParameters.find(PACKAGE_NAME));
        Assert.assertEquals("ProjectToDescription.with(AuthorProjectionActor.class, \"Event name here\", \"Another Event name here\"),", providerTemplateDataParameters.<List<ProjectToDescriptionParameter>>find(PROJECTION_TO_DESCRIPTION).get(0).getInitializationCommand());
        Assert.assertEquals("ProjectToDescription.with(BookProjectionActor.class, \"Event name here\", \"Another Event name here\")", providerTemplateDataParameters.<List<ProjectToDescriptionParameter>>find(PROJECTION_TO_DESCRIPTION).get(1).getInitializationCommand());

        //Assert for Projections

        final List<TemplateData> projectionsTemplatesData =
                allTemplatesData.stream()
                        .filter(templateData ->
                                templateData.standard().equals(PROJECTION))
                        .collect(Collectors.toList());

        IntStream.range(0, 1).forEach(templateIndex -> {
            final String rootName = templateIndex == 0 ? "Author" : "Book";
            final String expectedName = rootName + "ProjectionActor";
            final String expectedStateName = rootName + "State";
            final String expectedEntityDataName = rootName + "Data";
            final String expectedStateQualifiedName = "io.vlingo.xoomapp.model." + expectedStateName;
            final String expectedEntityDataQualifiedName = EXPECTED_INFRA_PACKAGE + "." + expectedEntityDataName;
            final TemplateData projectionTemplateData = projectionsTemplatesData.get(templateIndex);
            final CodeTemplateParameters projectionTemplateDataParameters = projectionTemplateData.parameters();
            Assert.assertEquals(EXPECTED_PERSISTENCE_PACKAGE, projectionTemplateDataParameters.find(PACKAGE_NAME));
            Assert.assertEquals(expectedName, projectionTemplateDataParameters.find(PROJECTION_NAME));
            Assert.assertEquals(expectedStateName, projectionTemplateDataParameters.find(STATE_NAME));
            Assert.assertEquals(expectedEntityDataName, projectionTemplateDataParameters.find(ENTITY_DATA_NAME));
            Assert.assertEquals(expectedStateQualifiedName, projectionTemplateDataParameters.<List<ImportParameter>>find(IMPORTS).get(0).getQualifiedClassName());
            Assert.assertEquals(expectedEntityDataQualifiedName, projectionTemplateDataParameters.<List<ImportParameter>>find(IMPORTS).get(1).getQualifiedClassName());
            Assert.assertEquals(Paths.get(PERSISTENCE_PACKAGE_PATH, expectedName + ".java").toString(), projectionTemplateData.file().getAbsolutePath());
        });

        //Assert for EntityData

        final List<TemplateData> entitiesTemplatesData =
                allTemplatesData.stream()
                        .filter(templateData ->
                                templateData.standard().equals(ENTITY_DATA))
                        .collect(Collectors.toList());

        IntStream.range(0, 1).forEach(templateIndex -> {
            final String rootName = templateIndex == 0 ? "Author" : "Book";
            final String expectedName = rootName + "Data";
            final String expectedStateQualifiedName = "io.vlingo.xoomapp.model." + rootName + "State";
            final String expectedEntityDataQualifiedName = EXPECTED_INFRA_PACKAGE + "." + expectedName;
            final TemplateData entityDataTemplateData = entitiesTemplatesData.get(templateIndex);
            final CodeTemplateParameters entityDataTemplateDataParameters = entityDataTemplateData.parameters();
            Assert.assertEquals(EXPECTED_INFRA_PACKAGE, entityDataTemplateDataParameters.find(PACKAGE_NAME));
            Assert.assertEquals(expectedName, entityDataTemplateDataParameters.find(ENTITY_DATA_NAME));
            Assert.assertEquals(expectedEntityDataQualifiedName, entityDataTemplateDataParameters.find(ENTITY_DATA_QUALIFIED_CLASS_NAME));
            Assert.assertEquals(expectedStateQualifiedName, entityDataTemplateDataParameters.find(STATE_QUALIFIED_CLASS_NAME));
            Assert.assertEquals(Paths.get(INFRASTRUCTURE_PACKAGE_PATH, expectedName + ".java").toString(), entityDataTemplateData.file().getAbsolutePath());
        });
    }

    @Test
    public void testOperationBasedProjectionTemplateDataBuild() {
        final List<TemplateData> allTemplatesData =
                ProjectionTemplateDataFactory.build("io.vlingo.xoomapp", PROJECT_PATH,
                        ProjectionType.OPERATION_BASED, contents());

        //General Assert

        Assert.assertEquals(5, allTemplatesData.size());
        Assert.assertEquals(1, allTemplatesData.stream().filter(templateData -> templateData.standard().equals(PROJECTION_DISPATCHER_PROVIDER)).count());
        Assert.assertEquals(2, allTemplatesData.stream().filter(templateData -> templateData.standard().equals(PROJECTION)).count());
        Assert.assertEquals(2, allTemplatesData.stream().filter(templateData -> templateData.standard().equals(ENTITY_DATA)).count());

        //Assert for ProjectionDispatcherProvider

        final TemplateData providerTemplateData = allTemplatesData.stream().filter(templateData -> templateData.standard().equals(PROJECTION_DISPATCHER_PROVIDER)).findFirst().get();
        final CodeTemplateParameters providerTemplateDataParameters = providerTemplateData.parameters();
        Assert.assertEquals(EXPECTED_PERSISTENCE_PACKAGE, providerTemplateDataParameters.find(PACKAGE_NAME));
        Assert.assertEquals("ProjectToDescription.with(AuthorProjectionActor.class, \"Operation name here\", \"Another Operation name here\"),", providerTemplateDataParameters.<List<ProjectToDescriptionParameter>>find(PROJECTION_TO_DESCRIPTION).get(0).getInitializationCommand());
        Assert.assertEquals("ProjectToDescription.with(BookProjectionActor.class, \"Operation name here\", \"Another Operation name here\")", providerTemplateDataParameters.<List<ProjectToDescriptionParameter>>find(PROJECTION_TO_DESCRIPTION).get(1).getInitializationCommand());
    }

    private List<Content> contents() {
        return Arrays.asList(
                Content.with(STATE, new File(Paths.get(MODEL_PACKAGE_PATH, "author", "AuthorState.java").toString()), AUTHOR_STATE_CONTENT_TEXT),
                Content.with(STATE, new File(Paths.get(MODEL_PACKAGE_PATH, "book", "BookState.java").toString()), BOOK_STATE_CONTENT_TEXT),
                Content.with(AGGREGATE_PROTOCOL, new File(Paths.get(MODEL_PACKAGE_PATH, "author", "Author.java").toString()), AUTHOR_CONTENT_TEXT),
                Content.with(AGGREGATE_PROTOCOL, new File(Paths.get(MODEL_PACKAGE_PATH, "book", "Book.java").toString()), BOOK_CONTENT_TEXT)
        );
    }

    private static final String EXPECTED_INFRA_PACKAGE = "io.vlingo.xoomapp.infrastructure";
    private static final String EXPECTED_PERSISTENCE_PACKAGE = "io.vlingo.xoomapp.infrastructure.persistence";

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
            Paths.get(PROJECT_PATH, "src", "main", "java",
                    "io", "vlingo", "xoomapp", "infrastructure", "persistence").toString();

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
