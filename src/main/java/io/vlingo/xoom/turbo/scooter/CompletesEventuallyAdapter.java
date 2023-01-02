// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.scooter;

import io.vlingo.xoom.actors.Address;
import io.vlingo.xoom.actors.CompletesEventually;
import io.vlingo.xoom.common.Completes;

public class CompletesEventuallyAdapter implements CompletesEventually {
  private final Completes<?> completes;

  public CompletesEventuallyAdapter(final Completes<?> completes) {
    this.completes = completes;
  }

  @Override
  public Address address() {
    return NoAddress.NoAddress;
  }

  @Override
  public void with(Object outcome) {
    completes.with(outcome);
  }
}
