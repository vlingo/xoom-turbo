// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.scooter.model.object;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EmployeeEntityTest {
  @Test
  public void testThatEmployeeIdentifiesModifiesRecovers() {
    final EmployeeEntity employee = new EmployeeEntity();

    employee.hire("12345", 50000);
    final EmployeeState state1 = employee.applied().state;
    assertTrue(state1.persistenceId() > 0);
    assertEquals("12345", state1.number);
    assertEquals(50000, state1.salary);

    employee.assign("67890");
    final EmployeeState state2 = employee.applied().state;
    assertEquals(state1.persistenceId(), state2.persistenceId());
    assertEquals("67890", state2.number);
    assertEquals(50000, state2.salary);

    employee.adjust(55000);
    final EmployeeState state3 = employee.applied().state;
    assertEquals(state1.persistenceId(), state3.persistenceId());
    assertEquals("67890", state3.number);
    assertEquals(55000, state3.salary);
  }
}
