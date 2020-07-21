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
        Assert.assertEquals("Order.java", context.contents().get(0).file.getName());
        Assert.assertEquals("OrderEntity.java", context.contents().get(1).file.getName());
        Assert.assertEquals("OrderState.java", context.contents().get(2).file.getName());
        Assert.assertEquals("OrderPlaceholderDefined.java", context.contents().get(3).file.getName());
        Assert.assertEquals("OrderCreated.java", context.contents().get(4).file.getName());
        Assert.assertEquals("OrderCancelled.java", context.contents().get(5).file.getName());
        Assert.assertEquals("Product.java", context.contents().get(6).file.getName());
        Assert.assertEquals("ProductEntity.java", context.contents().get(7).file.getName());
        Assert.assertEquals("ProductState.java", context.contents().get(8).file.getName());
        Assert.assertEquals("ProductPlaceholderDefined.java", context.contents().get(9).file.getName());
        Assert.assertEquals("ProductSoldOut.java", context.contents().get(10).file.getName());

        Assert.assertTrue(context.contents().get(0).text.contains("interface Order "));
        Assert.assertTrue(context.contents().get(1).text.contains("class OrderEntity extends StatefulEntity"));
        Assert.assertTrue(context.contents().get(2).text.contains("class OrderState extends StateObject"));
        Assert.assertTrue(context.contents().get(3).text.contains("class OrderPlaceholderDefined extends DomainEvent"));
        Assert.assertTrue(context.contents().get(4).text.contains("class OrderCreated extends DomainEvent"));
        Assert.assertTrue(context.contents().get(5).text.contains("class OrderCancelled extends DomainEvent"));
        Assert.assertTrue(context.contents().get(6).text.contains("interface Product "));
        Assert.assertTrue(context.contents().get(7).text.contains("class ProductEntity extends StatefulEntity"));
        Assert.assertTrue(context.contents().get(8).text.contains("class ProductState extends StateObject"));
        Assert.assertTrue(context.contents().get(9).text.contains("class ProductPlaceholderDefined extends DomainEvent"));
        Assert.assertTrue(context.contents().get(10).text.contains("class ProductSoldOut extends DomainEvent"));
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
