package io.vlingo.xoom.codegen.template.model;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameters;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateData;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static io.vlingo.xoom.codegen.parameter.Label.PACKAGE;
import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.DOMAIN_EVENT;
import static io.vlingo.xoom.codegen.template.storage.StorageType.JOURNAL;

public class ModelTemplateDataFactoryTest {

    @Test
    public void testAggregateTemplateDataBuild() {
        final CodeGenerationParameters parameters =
                CodeGenerationParameters.from(CodeGenerationParameter.of(PACKAGE, "io.vlingo.xoomapp"),
                        CodeGenerationParameter.of(Label.STORAGE_TYPE, JOURNAL),
                        authorAggregate());

        final List<TemplateData> templatesData = ModelTemplateDataFactory.from(parameters);

        Assert.assertEquals(5, templatesData.size());
        Assert.assertEquals(2, templatesData.stream().filter(templateData -> templateData.hasStandard(DOMAIN_EVENT)).count());

        Assert.assertEquals("Author", templatesData.get(0).parameters().find(AGGREGATE_PROTOCOL_NAME));
        Assert.assertEquals(JOURNAL, templatesData.get(1).parameters().find(STORAGE_TYPE));
        Assert.assertEquals("io.vlingo.xoomapp.model.author", templatesData.get(1).parameters().find(PACKAGE_NAME));
        Assert.assertEquals("Author.java", templatesData.get(0).filename());
        Assert.assertEquals("AuthorEntity.java", templatesData.get(1).filename());
        Assert.assertEquals("AuthorState.java", templatesData.get(2).filename());

        Assert.assertEquals("AuthorRegistered", templatesData.get(3).parameters().find(DOMAIN_EVENT_NAME));
        Assert.assertEquals("io.vlingo.xoomapp.model.author", templatesData.get(3).parameters().find(PACKAGE_NAME));
        Assert.assertEquals("AuthorRegistered.java", templatesData.get(3).filename());

        Assert.assertEquals("AuthorRanked", templatesData.get(4).parameters().find(DOMAIN_EVENT_NAME));
        Assert.assertEquals("io.vlingo.xoomapp.model.author", templatesData.get(4).parameters().find(PACKAGE_NAME));
        Assert.assertEquals("AuthorRanked.java", templatesData.get(4).filename());


//        Assert.assertEquals("Book", templatesData.get(6).parameters().find(AGGREGATE_PROTOCOL_NAME));
//        Assert.assertEquals(JOURNAL, templatesData.get(7).parameters().find(STORAGE_TYPE));
//        Assert.assertEquals("io.vlingo.xoomapp.model.book", templatesData.get(7).parameters().find(PACKAGE_NAME));
//        Assert.assertEquals("Book.java", templatesData.get(6).filename());
//        Assert.assertEquals("BookEntity.java", templatesData.get(7).filename());
//        Assert.assertEquals("BookState.java", templatesData.get(8).filename());
//        Assert.assertEquals("BookPlaceholderDefined.java", templatesData.get(9).filename());
//
//        Assert.assertEquals("BookPurchased", templatesData.get(10).parameters().find(DOMAIN_EVENT_NAME));
//        Assert.assertEquals("io.vlingo.xoomapp.model.book", templatesData.get(10).parameters().find(PACKAGE_NAME));
//        Assert.assertEquals("BookPurchased.java", templatesData.get(10).filename());
//
//        Assert.assertEquals("BookPublished", templatesData.get(11).parameters().find(DOMAIN_EVENT_NAME));
//        Assert.assertEquals("io.vlingo.xoomapp.model.book", templatesData.get(11).parameters().find(PACKAGE_NAME));
//        Assert.assertEquals("BookPublished.java", templatesData.get(11).filename());
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

        final CodeGenerationParameter authorRegisteredEvent =
                CodeGenerationParameter.of(Label.DOMAIN_EVENT, "AuthorRegistered")
                        .relate(idField).relate(nameField);

        final CodeGenerationParameter authorRankedEvent =
                CodeGenerationParameter.of(Label.DOMAIN_EVENT, "AuthorRanked")
                        .relate(idField).relate(rankField);

        final CodeGenerationParameter factoryMethod =
                CodeGenerationParameter.of(Label.AGGREGATE_METHOD, "withName")
                        .relate(Label.METHOD_PARAMETER, "name")
                        .relate(Label.FACTORY_METHOD, "true")
                        .relate(authorRegisteredEvent);

        final CodeGenerationParameter rankMethod =
                CodeGenerationParameter.of(Label.AGGREGATE_METHOD, "changeRank")
                        .relate(Label.METHOD_PARAMETER, "rank")
                        .relate(authorRankedEvent);

        return CodeGenerationParameter.of(Label.AGGREGATE, "Author")
                .relate(idField).relate(nameField).relate(rankField)
                .relate(factoryMethod).relate(rankMethod)
                .relate(authorRegisteredEvent).relate(authorRankedEvent);
    }

}
