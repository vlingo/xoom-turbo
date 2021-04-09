// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.scooter.persistence;

import io.vlingo.xoom.symbio.store.state.StateStore;

public class EntityRepository extends StatefulRepository {
  private final StateStore store;

  public EntityRepository(final StateStore store) {
    this.store = store;
  }

  public Entity1 entity1Of(final String id) {
    final ReadInterest interest = readInterest();
    store.read(id, Entity1.class, interest);
    return await(interest);
  }

  public Entity2 entity2Of(final String id) {
    final ReadInterest interest = readInterest();
    store.read(id, Entity2.class, interest);
    return await(interest);
  }

  public void save(Entity1 entity) {
    final WriteInterest interest = writeInterest();
    store.write(entity.id, entity, 1, interest);
    await(interest);
  }

  public void save(Entity2 entity) {
    final WriteInterest interest = writeInterest();
    store.write(entity.id, entity, 1, interest);
    await(interest);
  }
}
