// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.scooter.model.sourced;

import io.vlingo.xoom.lattice.model.DomainEvent;
import io.vlingo.xoom.symbio.Source;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class EventSourcedEntityTest {

  @Test
  public void testProductDefinedEventKept() throws Exception {
    final Product product = new Product("dice", "fuz", "dice-fuz-1", "Fuzzy dice.", 999);
    assertEquals(3, product.applied().size());
    assertEquals("dice-fuz-1", product.name);
    assertEquals("Fuzzy dice.", product.description);
    assertEquals(999, product.price);
    assertEquals(new ProductDefined("dice-fuz-1", "Fuzzy dice.", 999), product.applied().sourceAt(2));
  }

  @Test
  public void testProductNameChangedEventKept() throws Exception {
    final Product product = new Product("dice", "fuz", "dice-fuz-1", "Fuzzy dice.", 999);

    product.changeName("dice-fuzzy-1");
    assertEquals(4, product.applied().size());
    assertEquals("dice-fuzzy-1", product.name);
    assertEquals(new ProductNameChanged("dice-fuzzy-1"), product.applied().sourceAt(3));
  }

  @Test
  public void testProductDescriptionChangedEventsKept() throws Exception {
    final Product product = new Product("dice", "fuz", "dice-fuz-1", "Fuzzy dice.", 999);

    product.changeDescription("Fuzzy dice, and all.");
    assertEquals(4, product.applied().size());
    assertEquals("Fuzzy dice, and all.", product.description);
    assertEquals(new ProductDescriptionChanged("Fuzzy dice, and all."), product.applied().sourceAt(3));
  }

  @Test
  public void testProductPriceChangedEventKept() throws Exception {
    final Product product = new Product("dice", "fuz", "dice-fuz-1", "Fuzzy dice.", 999);

    product.changePrice(995);
    assertEquals(4, product.applied().size());
    assertEquals(995, product.price);
    assertEquals(new ProductPriceChanged(995), product.applied().sourceAt(3));
  }

  @Test
  public void testReconstitution() throws Exception {
    final List<Source<DomainEvent>> sources = new ArrayList<>();

    final Product product = new Product("dice", "fuz", "dice-fuz-1", "Fuzzy dice.", 999);
    sources.addAll(product.applied().sources());
    product.changeName("dice-fuzzy-1");
    sources.addAll(product.applied().sources());
    product.changeDescription("Fuzzy dice, and all.");
    sources.addAll(product.applied().sources());
    product.changePrice(995);
    sources.addAll(product.applied().sources());

    final Product productAgain = new Product(sources, sources.size());
    assertEquals(product, productAgain);
  }

  @Test
  public void testBaseClassBehavior() {
    final Product product = new Product("dice", "fuz", "dice-fuz-1", "Fuzzy dice.", 999);
    assertEquals(new ProductGrandparentTyped("dice"), product.applied().sourceAt(0));
    assertEquals(new ProductParentCategorized("fuz"), product.applied().sourceAt(1));
    assertEquals(new ProductDefined("dice-fuz-1", "Fuzzy dice.", 999), product.applied().sourceAt(2));
  }

  public static abstract class ProductGrandparent extends EventSourcedEntity {
    private String type;

    protected ProductGrandparent(final List<Source<DomainEvent>> eventStream, final int streamVersion) {
      super(eventStream, streamVersion);
    }

    protected ProductGrandparent(final String type) {
      apply(new ProductGrandparentTyped(type));
    }

    @Override
    public String toString() {
      return "Grandparent [type=" + type + "]";
    }

    private void whenProductGrandparentTyped(final ProductGrandparentTyped event) {
      this.type = event.type;
    }

    static {
      registerConsumer(ProductGrandparent.class, ProductGrandparentTyped.class, ProductGrandparent::whenProductGrandparentTyped);
    }
  }

  public static abstract class ProductParent extends ProductGrandparent {
    private String category;

    protected ProductParent(final List<Source<DomainEvent>> eventStream, final int streamVersion) {
      super(eventStream, streamVersion);
    }

    protected ProductParent(final String type, final String category) {
      super(type);

      apply(new ProductParentCategorized(category));
    }

    @Override
    public String toString() {
      return "ProductParent [category=" + category + "]";
    }

    private void whenProductParentCategorized(final ProductParentCategorized event) {
      this.category = event.category;
    }

    static {
      registerConsumer(ProductParent.class, ProductParentCategorized.class, ProductParent::whenProductParentCategorized);
    }
  }

  public static class Product extends ProductParent {
    public String name;
    public String description;
    public long price;

    public Product(final String type, final String category, final String name, final String description, final long price) {
      super(type, category);

      apply(new ProductDefined(name, description, price));
    }

    public Product(final List<Source<DomainEvent>> eventStream, final int streamVersion) {
      super(eventStream, streamVersion);
    }

    public void changeDescription(final String description) {
      apply(new ProductDescriptionChanged(description));
    }

    public void changeName(final String name) {
      apply(new ProductNameChanged(name));
    }

    public void changePrice(final long price) {
      apply(new ProductPriceChanged(price));
    }

    @Override
    public boolean equals(Object other) {
      if (other == null || other.getClass() != Product.class) {
        return false;
      }

      final Product otherProduct = (Product) other;

      return this.name.equals(otherProduct.name) &&
          this.description.equals(otherProduct.description) &&
          this.price == otherProduct.price;
    }

    public void whenProductDefined(final ProductDefined event) {
      this.name = event.name;
      this.description = event.description;
      this.price = event.price;
    }

    public void whenProductDescriptionChanged(final ProductDescriptionChanged event) {
      this.description = event.description;
    }

    public void whenProductNameChanged(final ProductNameChanged event) {
      this.name = event.name;
    }

    public void whenProductPriceChanged(final ProductPriceChanged event) {
      this.price = event.price;
    }

    @Override
    protected String streamName() {
      return null;
    }

    @Override
    public String id() {
      return null;
    }

    static {
      registerConsumer(Product.class, ProductDefined.class, Product::whenProductDefined);
      registerConsumer(Product.class, ProductDescriptionChanged.class, Product::whenProductDescriptionChanged);
      registerConsumer(Product.class, ProductNameChanged.class, Product::whenProductNameChanged);
      registerConsumer(Product.class, ProductPriceChanged.class, Product::whenProductPriceChanged);
    }
  }

  public static final class ProductGrandparentTyped extends DomainEvent {
    public final String type;

    public ProductGrandparentTyped(final String type) {
      this.type = type;
    }
  }

  public static final class ProductParentCategorized extends DomainEvent {
    public final String category;

    public ProductParentCategorized(final String category) {
      this.category = category;
    }
  }

  public static final class ProductDefined extends DomainEvent {
    public final String description;
    public final String name;
    public final Date occurredOn;
    public final long price;
    public final int version;

    ProductDefined(final String name, final String description, final long price) {
      this.name = name;
      this.description = description;
      this.price = price;
      this.occurredOn = new Date();
      this.version = 1;
    }

    public Date occurredOn() {
      return occurredOn;
    }

    public int eventVersion() {
      return version;
    }

    @Override
    public boolean equals(Object other) {
      if (other == null || other.getClass() != ProductDefined.class) {
        return false;
      }

      final ProductDefined otherProductDefined = (ProductDefined) other;

      return this.name.equals(otherProductDefined.name) &&
          this.description.equals(otherProductDefined.description) &&
          this.price == otherProductDefined.price &&
          this.version == otherProductDefined.version;
    }
  }

  public static final class ProductDescriptionChanged extends DomainEvent {
    public final String description;
    public final Date occurredOn;
    public final int version;

    ProductDescriptionChanged(final String description) {
      this.description = description;
      this.occurredOn = new Date();
      this.version = 1;
    }

    public Date occurredOn() {
      return occurredOn;
    }

    public int eventVersion() {
      return version;
    }

    @Override
    public boolean equals(Object other) {
      if (other == null || other.getClass() != ProductDescriptionChanged.class) {
        return false;
      }

      final ProductDescriptionChanged otherProductDescriptionChanged = (ProductDescriptionChanged) other;

      return this.description.equals(otherProductDescriptionChanged.description) &&
          this.version == otherProductDescriptionChanged.version;
    }
  }

  public static final class ProductNameChanged extends DomainEvent {
    public final String name;
    public final Date occurredOn;
    public final int version;

    ProductNameChanged(final String name) {
      this.name = name;
      this.occurredOn = new Date();
      this.version = 1;
    }

    public Date occurredOn() {
      return occurredOn;
    }

    public int eventVersion() {
      return version;
    }

    @Override
    public boolean equals(Object other) {
      if (other == null || other.getClass() != ProductNameChanged.class) {
        return false;
      }

      final ProductNameChanged otherProductNameChanged = (ProductNameChanged) other;

      return this.name.equals(otherProductNameChanged.name) &&
          this.version == otherProductNameChanged.version;
    }
  }

  public static final class ProductPriceChanged extends DomainEvent {
    public final long price;
    public final Date occurredOn;
    public final int version;

    ProductPriceChanged(final long price) {
      this.price = price;
      this.occurredOn = new Date();
      this.version = 1;
    }

    public Date occurredOn() {
      return occurredOn;
    }

    public int eventVersion() {
      return version;
    }

    @Override
    public boolean equals(Object other) {
      if (other == null || other.getClass() != ProductPriceChanged.class) {
        return false;
      }

      final ProductPriceChanged otherProductPriceChanged = (ProductPriceChanged) other;

      return this.price == otherProductPriceChanged.price &&
          this.version == otherProductPriceChanged.version;
    }
  }
}
