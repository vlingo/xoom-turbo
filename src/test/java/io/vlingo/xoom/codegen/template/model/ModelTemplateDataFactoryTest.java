package io.vlingo.xoom.codegen.template.model;

import io.vlingo.xoom.codegen.template.TemplateData;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.DOMAIN_EVENT;
import static io.vlingo.xoom.codegen.template.storage.StorageType.JOURNAL;

public class ModelTemplateDataFactoryTest {

    @Test
    public void testAggregateTemplateDataBuild() {
        final String basePackage = "io.vlingo.xoomapp";
        final String aggregatesData =
                "Author;AuthorAccepted;AuthorRegistered|Book;BookPurchased;BookPublished";

        final List<TemplateData> templatesData =
                ModelTemplateDataFactory.build(basePackage, aggregatesData, JOURNAL);

        Assert.assertEquals(12, templatesData.size());
        Assert.assertEquals(6, templatesData.stream().filter(templateData -> templateData.standard().equals(DOMAIN_EVENT)).count());

        Assert.assertEquals("Author", templatesData.get(0).parameters().find(AGGREGATE_PROTOCOL_NAME));
        Assert.assertEquals(JOURNAL, templatesData.get(1).parameters().find(STORAGE_TYPE));
        Assert.assertEquals("io.vlingo.xoomapp.model.author", templatesData.get(1).parameters().find(PACKAGE_NAME));
        Assert.assertEquals("Author.java", templatesData.get(0).filename());
        Assert.assertEquals("AuthorEntity.java", templatesData.get(1).filename());
        Assert.assertEquals("AuthorState.java", templatesData.get(2).filename());
        Assert.assertEquals("AuthorPlaceholderDefined.java", templatesData.get(3).filename());

        Assert.assertEquals("AuthorAccepted", templatesData.get(4).parameters().find(DOMAIN_EVENT_NAME));
        Assert.assertEquals("io.vlingo.xoomapp.model.author", templatesData.get(4).parameters().find(PACKAGE_NAME));
        Assert.assertEquals("AuthorAccepted.java", templatesData.get(4).filename());

        Assert.assertEquals("AuthorRegistered", templatesData.get(5).parameters().find(DOMAIN_EVENT_NAME));
        Assert.assertEquals("io.vlingo.xoomapp.model.author", templatesData.get(5).parameters().find(PACKAGE_NAME));
        Assert.assertEquals("AuthorRegistered.java", templatesData.get(5).filename());

        Assert.assertEquals("Book", templatesData.get(6).parameters().find(AGGREGATE_PROTOCOL_NAME));
        Assert.assertEquals(JOURNAL, templatesData.get(7).parameters().find(STORAGE_TYPE));
        Assert.assertEquals("io.vlingo.xoomapp.model.book", templatesData.get(7).parameters().find(PACKAGE_NAME));
        Assert.assertEquals("Book.java", templatesData.get(6).filename());
        Assert.assertEquals("BookEntity.java", templatesData.get(7).filename());
        Assert.assertEquals("BookState.java", templatesData.get(8).filename());
        Assert.assertEquals("BookPlaceholderDefined.java", templatesData.get(9).filename());

        Assert.assertEquals("BookPurchased", templatesData.get(10).parameters().find(DOMAIN_EVENT_NAME));
        Assert.assertEquals("io.vlingo.xoomapp.model.book", templatesData.get(10).parameters().find(PACKAGE_NAME));
        Assert.assertEquals("BookPurchased.java", templatesData.get(10).filename());

        Assert.assertEquals("BookPublished", templatesData.get(11).parameters().find(DOMAIN_EVENT_NAME));
        Assert.assertEquals("io.vlingo.xoomapp.model.book", templatesData.get(11).parameters().find(PACKAGE_NAME));
        Assert.assertEquals("BookPublished.java", templatesData.get(11).filename());
    }

}
