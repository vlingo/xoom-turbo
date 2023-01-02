// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.scooter.model.object;

import io.vlingo.xoom.lattice.model.DomainEvent;

public class EmployeeEntity extends ObjectEntity<EmployeeState,DomainEvent> implements Employee {
  private EmployeeState employee;

  public EmployeeEntity() {
    this.employee = new EmployeeState(); // unidentified
  }

  public EmployeeEntity(final long id) {
    this.employee = new EmployeeState(id, "", 0); // recover
  }

  @Override
  public void assign(final String number) {
    apply(employee.with(number), new EmployeeNumberAssigned());
  }

  @Override
  public void adjust(final int salary) {
    apply(employee.with(salary), new EmployeeSalaryAdjusted());
  }

  @Override
  public void hire(final String number, final int salary) {
    apply(employee.with(number).with(salary), new EmployeeHired());
  }

  @Override
  public String id() {
    return String.valueOf(employee.persistenceId());
  }

  @Override
  protected void stateObject(final EmployeeState stateObject) {
    this.employee = stateObject;
  }
}
