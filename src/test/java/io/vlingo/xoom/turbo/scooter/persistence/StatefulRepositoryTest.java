// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.scooter.persistence;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.TestWorld;
import io.vlingo.xoom.symbio.EntryAdapterProvider;
import io.vlingo.xoom.symbio.StateAdapterProvider;
import io.vlingo.xoom.symbio.store.state.StateStore;
import io.vlingo.xoom.symbio.store.state.StateTypeStateStoreMap;
import io.vlingo.xoom.symbio.store.state.inmemory.InMemoryStateStoreActor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class StatefulRepositoryTest {
  private final static String StoreName1 = Entity1.class.getSimpleName();
  private final static String StoreName2 = Entity2.class.getSimpleName();

  private MockStateStoreDispatcher dispatcher;
  private MockStateStoreResultInterest interest;
  private EntityRepository repository;
  private StateStore store;
  private TestWorld testWorld;
  private World world;

  @Test
  public void testThatWriteReadAwaits() {
    dispatcher.afterCompleting(0);

    final String entity1Id = "123";

    final Entity1 entity1_1 = new Entity1(entity1Id, 123);

    repository.save(entity1_1);

    final Entity1 entity1_2 = repository.entity1Of(entity1Id);

    Assert.assertEquals(entity1_1, entity1_2);
    Assert.assertEquals(entity1_1.value, entity1_2.value);

    final String entity2Id = "456";

    final Entity2 entity2_1 = new Entity2(entity2Id, "789");

    repository.save(entity2_1);

    final Entity2 entity2_2 = repository.entity2Of(entity2Id);

    Assert.assertEquals(entity2_1.id, entity2_2.id);
    Assert.assertEquals(entity2_1.value, entity2_2.value);
  }

  @Before
  public void setUp() {
    testWorld = TestWorld.startWithDefaults("test-store");
    world = testWorld.world();

    interest = new MockStateStoreResultInterest();
    dispatcher = new MockStateStoreDispatcher(interest);

    final StateAdapterProvider stateAdapterProvider = new StateAdapterProvider(world);
    new EntryAdapterProvider(world);

    stateAdapterProvider.registerAdapter(Entity1.class, new Entity1StateAdapter());
    // NOTE: No adapter registered for Entity2.class because it will use the default

    store = world.actorFor(StateStore.class, InMemoryStateStoreActor.class, Arrays.asList(dispatcher));

    StateTypeStateStoreMap.stateTypeToStoreName(Entity1.class, StoreName1);
    StateTypeStateStoreMap.stateTypeToStoreName(Entity2.class, StoreName2);

    repository = new EntityRepository(store);
  }

}
