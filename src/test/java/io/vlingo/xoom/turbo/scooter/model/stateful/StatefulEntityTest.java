// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.scooter.model.stateful;

import io.vlingo.xoom.common.serialization.JsonSerialization;
import io.vlingo.xoom.lattice.model.DomainEvent;
import io.vlingo.xoom.symbio.Metadata;
import io.vlingo.xoom.symbio.State;
import io.vlingo.xoom.symbio.State.TextState;
import io.vlingo.xoom.symbio.StateAdapter;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StatefulEntityTest {
  private final Random idGenerator = new Random();

  @Test
  public void testThatStatefulEntityPreservesRestores() throws Exception {
    final String entityId = "" + idGenerator.nextInt(10_000);
    final Entity1State state = new Entity1State(entityId, "Sally", 23);

    final Entity1 entity1 = new Entity1Entity(state);

    assertEquals(state, entity1.state());

    entity1.changeName("Sally Jane");

    Entity1State newState = entity1.state();

    assertEquals("Sally Jane", newState.name);

    entity1.increaseAge();

    newState = entity1.state();

    assertEquals(24, newState.age);

    final Entity1State identityState = new Entity1State(entityId);

    final Entity1 restoredEntity1 = new Entity1Entity(identityState);

    final Entity1State restoredEntity1State = restoredEntity1.state();

    assertNotNull(restoredEntity1State);
  }

  @Test
  public void testThatMetadataCallbackPreservesRestores() throws Exception {
    final String entityId = "" + idGenerator.nextInt(10_000);
    final Entity1State state = new Entity1State(entityId, "Sally", 23);

    final Entity1 entity1 = new Entity1Entity(state);

    final Entity1State current1 = entity1.state();

    assertEquals(state, current1);

    entity1.changeName("Sally Jane");

    Entity1State newState = entity1.state();

    assertEquals("Sally Jane", newState.name);

    entity1.increaseAge();

    newState = entity1.state();

    assertEquals(24, newState.age);

    final Entity1State identityState = new Entity1State(entityId);

    final Entity1 restoredEntity1 = new Entity1Entity(identityState);

    final Entity1State restoredEntity1State = restoredEntity1.state();

    assertNotNull(restoredEntity1State);
  }

  public static class Entity1StateAdapter implements StateAdapter<Entity1State,State<String>> {
    @Override public int typeVersion() { return 1; }

    @Override
    public Entity1State fromRawState(final State<String> raw) {
      return JsonSerialization.deserialized(raw.data, Entity1State.class);
    }

    @Override
    public <ST> ST fromRawState(final State<String> raw, final Class<ST> stateType) {
      return JsonSerialization.deserialized(raw.data, stateType);
    }

    @Override
    public State<String> toRawState(final Entity1State state, final int stateVersion) {
      return toRawState(state.id, state, stateVersion, Metadata.nullMetadata());
    }

    @Override
    public State<String> toRawState(final String id, final Entity1State state, final int stateVersion, final Metadata metadata) {
      final String serialization = JsonSerialization.serialized(state);
      return new TextState(id, Entity1State.class, typeVersion(), serialization, stateVersion);
    }
  }

  public static interface Entity1 {
    void changeName(final String name);
    void increaseAge();
    Entity1State state();
  }

  public static class Entity1State {
    public final String id;
    public final String name;
    public final int age;

    public Entity1State(final String id, final String name, final int age) {
      this.id = id;
      this.name = name;
      this.age = age;
    }

    public Entity1State(final String id) {
      this(id, null, 0);
    }

    public Entity1State copy() {
      return new Entity1State(id, name, age);
    }

    public boolean hasState() {
      return id != null && name != null && age > 0;
    }

    @Override
    public String toString() {
      return "Entity1State[id=" + id + " name=" + name + " age=" + age + "]";
    }

    public Entity1State withName(final String name) {
      return new Entity1State(this.id, name, this.age);
    }

    public Entity1State withAge(final int age) {
      return new Entity1State(this.id, this.name, age);
    }

    @Override
    public boolean equals(final Object other) {
      if (other == null || other.getClass() != this.getClass()) {
        return false;
      }
      final Entity1State otherState = (Entity1State) other;
      return this.id.equals(otherState.id) && this.name.equals(otherState.name) && this.age == otherState.age;
    }
  }

  public static class Entity1Entity extends StatefulEntity<Entity1State,DomainEvent> implements Entity1 {
    private Entity1State state;

    public Entity1Entity(final Entity1State state) {
      this.state = state;
    }

    //===================================
    // Entity1
    //===================================

    @Override
    public void changeName(final String name) {
      apply(state.withName(name));
    }

    @Override
    public void increaseAge() {
      apply(state.withAge(state.age + 1));
    }

    //===================================
    // StatefulEntity
    //===================================

    @Override
    public String id() {
      return state.id;
    }

    @Override
    public Entity1State state() {
      return state;
    }

    @Override
    protected void state(final Entity1State state) {
      this.state = state;
    }
  }
}
