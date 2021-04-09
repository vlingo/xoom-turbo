// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.scooter;

import io.vlingo.xoom.actors.Address;

public class NoAddress implements Address {
  public static final Address NoAddress = new NoAddress();

  @Override
  public int compareTo(final Address other) {
    return -1;
  }

  @Override
  public long id() {
    return 0;
  }

  @Override
  public long idSequence() {
    return 0;
  }

  @Override
  public String idSequenceString() {
    return "0";
  }

  @Override
  public String idString() {
    return "0";
  }

  @Override
  public <T> T idTyped() {
    return null;
  }

  @Override
  public String name() {
    return "";
  }

  @Override
  public boolean isDistributable() {
    return false;
  }
}
