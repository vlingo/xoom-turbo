package io.vlingo.xoom.codegen.template.model;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.model.ModelGenerationStep;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.vlingo.xoom.codegen.CodeGenerationParameter.*;

public class ModelGenerationStepTest {

    @Test
    public void testModelGeneration() {
        final CodeGenerationContext context =
                CodeGenerationContext.with(loadParameters());

        new ModelGenerationStep().process(context);

        Assert.assertEquals(11, context.contents().size());
        Assert.assertEquals("Order", context.contents().get(0).retrieveClassName());
        Assert.assertEquals("OrderEntity", context.contents().get(1).retrieveClassName());
        Assert.assertEquals("OrderState", context.contents().get(2).retrieveClassName());
        Assert.assertEquals("OrderPlaceholderDefined", context.contents().get(3).retrieveClassName());
        Assert.assertEquals("OrderCreated", context.contents().get(4).retrieveClassName());
        Assert.assertEquals("OrderCancelled", context.contents().get(5).retrieveClassName());
        Assert.assertEquals("Product", context.contents().get(6).retrieveClassName());
        Assert.assertEquals("ProductEntity", context.contents().get(7).retrieveClassName());
        Assert.assertEquals("ProductState", context.contents().get(8).retrieveClassName());
        Assert.assertEquals("ProductPlaceholderDefined", context.contents().get(9).retrieveClassName());
        Assert.assertEquals("ProductSoldOut", context.contents().get(10).retrieveClassName());

        Assert.assertTrue(context.contents().get(0).contains("interface Order "));
        Assert.assertTrue(context.contents().get(1).contains("class OrderEntity extends StatefulEntity"));
        Assert.assertTrue(context.contents().get(2).contains("class OrderState extends StateObject"));
        Assert.assertTrue(context.contents().get(3).contains("class OrderPlaceholderDefined extends DomainEvent"));
        Assert.assertTrue(context.contents().get(4).contains("class OrderCreated extends DomainEvent"));
        Assert.assertTrue(context.contents().get(5).contains("class OrderCancelled extends DomainEvent"));
        Assert.assertTrue(context.contents().get(6).contains("interface Product "));
        Assert.assertTrue(context.contents().get(7).contains("class ProductEntity extends StatefulEntity"));
        Assert.assertTrue(context.contents().get(8).contains("class ProductState extends StateObject"));
        Assert.assertTrue(context.contents().get(9).contains("class ProductPlaceholderDefined extends DomainEvent"));
        Assert.assertTrue(context.contents().get(10).contains("class ProductSoldOut extends DomainEvent"));
    }

    private Map<CodeGenerationParameter, String> loadParameters() {
        final Map<CodeGenerationParameter, String> parameters = new HashMap<>();
        parameters.put(PACKAGE, "com.company.context");
        parameters.put(APPLICATION_NAME, "logistics");
        parameters.put(STORAGE_TYPE, "STATE_STORE");
        parameters.put(AGGREGATES, "Order;OrderCreated;OrderCancelled|Product;ProductSoldOut");
        parameters.put(TARGET_FOLDER, "/Projects/");
        return parameters;
    }

}
