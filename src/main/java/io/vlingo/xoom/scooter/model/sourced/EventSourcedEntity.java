// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.scooter.model.sourced;

import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.symbio.Source;

import java.util.List;

public abstract class EventSourcedEntity extends SourcedEntity<DomainEvent> {
  public EventSourcedEntity() {
    super();
  }

  public EventSourcedEntity(final List<Source<DomainEvent>> stream, final int currentVersion) {
    super(stream, currentVersion);
  }
}
