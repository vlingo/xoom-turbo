// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.scooter.model.object;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PersonEntityTest {
  @Test
  public void testThatPersonIdentifiesModifiesRecovers() {
    final PersonEntity person = new PersonEntity(1);

    person.identify("Tom Jones", 78);
    final PersonState state1 = person.applied().state;
    assertTrue(state1.persistenceId() > 0);
    assertEquals("Tom Jones", state1.name);
    assertEquals(78, state1.age);

    person.change("Tom J Jones");
    final PersonState state2 = person.applied().state;
    assertEquals(state1.persistenceId(), state2.persistenceId());
    assertEquals("Tom J Jones", state2.name);
    assertEquals(78, state2.age);

    person.increaseAge();
    final PersonState state3 = person.applied().state;
    assertEquals(state1.persistenceId(), state3.persistenceId());
    assertEquals("Tom J Jones", state3.name);
    assertEquals(79, state3.age);
  }
}
