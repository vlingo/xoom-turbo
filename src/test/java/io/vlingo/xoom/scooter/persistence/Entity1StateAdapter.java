// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.scooter.persistence;

import io.vlingo.common.serialization.JsonSerialization;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.State;
import io.vlingo.symbio.State.TextState;
import io.vlingo.symbio.StateAdapter;

public class Entity1StateAdapter implements StateAdapter<Entity1,State<String>> {
  @Override public int typeVersion() { return 1; }

  @Override
  public Entity1 fromRawState(final State<String> raw) {
    return JsonSerialization.deserialized(raw.data, Entity1.class);
  }

  @Override
  public <ST> ST fromRawState(final State<String> raw, final Class<ST> stateType) {
    return JsonSerialization.deserialized(raw.data, stateType);
  }

  @Override
  public State<String> toRawState(final Entity1 state, final int stateVersion) {
    return toRawState(state.id, state, stateVersion, Metadata.nullMetadata());
  }

  @Override
  public State<String> toRawState(final String id, final Entity1 state, final int stateVersion, final Metadata metadata) {
    final String serialization = JsonSerialization.serialized(state);
    return new TextState(id, Entity1.class, typeVersion(), serialization, stateVersion);
  }
}
