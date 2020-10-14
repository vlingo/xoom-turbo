package io.vlingo.xoom.codegen.template.model;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameters;
import io.vlingo.xoom.codegen.parameter.Label;
import org.junit.Assert;
import org.junit.Test;

import static io.vlingo.xoom.codegen.parameter.Label.FACTORY_METHOD;
import static io.vlingo.xoom.codegen.parameter.Label.PACKAGE;
import static io.vlingo.xoom.codegen.template.storage.StorageType.STATE_STORE;

public class ModelGenerationStepTest {

    @Test
    public void testModelGeneration() {
        final CodeGenerationParameters parameters =
                CodeGenerationParameters.from(CodeGenerationParameter.of(PACKAGE, "io.vlingo.xoomapp"),
                        CodeGenerationParameter.of(Label.STORAGE_TYPE, STATE_STORE),
                        authorAggregate());

        final CodeGenerationContext context =
                CodeGenerationContext.with(parameters);

        final ModelGenerationStep modelGenerationStep = new ModelGenerationStep();

        Assert.assertTrue(modelGenerationStep.shouldProcess(context));

        modelGenerationStep.process(context);

        Assert.assertEquals(5, context.contents().size());
        Assert.assertEquals("Author", context.contents().get(0).retrieveClassName());
        Assert.assertEquals("AuthorEntity", context.contents().get(1).retrieveClassName());
        Assert.assertEquals("AuthorState", context.contents().get(2).retrieveClassName());
        Assert.assertEquals("AuthorRegistered", context.contents().get(3).retrieveClassName());
        Assert.assertEquals("AuthorRanked", context.contents().get(4).retrieveClassName());

//        Assert.assertEquals("Product", context.contents().get(6).retrieveClassName());
//        Assert.assertEquals("ProductEntity", context.contents().get(7).retrieveClassName());
//        Assert.assertEquals("ProductState", context.contents().get(8).retrieveClassName());
//        Assert.assertEquals("ProductPlaceholderDefined", context.contents().get(9).retrieveClassName());
//        Assert.assertEquals("ProductSoldOut", context.contents().get(10).retrieveClassName());

        Assert.assertTrue(context.contents().get(0).contains("interface Author "));
        Assert.assertTrue(context.contents().get(1).contains("class AuthorEntity extends StatefulEntity"));
        Assert.assertTrue(context.contents().get(2).contains("class AuthorState extends StateObject"));
        Assert.assertTrue(context.contents().get(3).contains("class AuthorRegistered extends DomainEvent"));
        Assert.assertTrue(context.contents().get(4).contains("class AuthorRanked extends DomainEvent"));

//        Assert.assertTrue(context.contents().get(6).contains("interface Product "));
//        Assert.assertTrue(context.contents().get(7).contains("class ProductEntity extends StatefulEntity"));
//        Assert.assertTrue(context.contents().get(8).contains("class ProductState extends StateObject"));
//        Assert.assertTrue(context.contents().get(9).contains("class ProductPlaceholderDefined extends DomainEvent"));
//        Assert.assertTrue(context.contents().get(10).contains("class ProductSoldOut extends DomainEvent"));
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
                        .relate(FACTORY_METHOD, "true")
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
