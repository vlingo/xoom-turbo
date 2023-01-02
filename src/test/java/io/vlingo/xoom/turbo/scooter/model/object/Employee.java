// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.scooter.model.object;

import io.vlingo.xoom.lattice.model.DomainEvent;

public interface Employee {
  void assign(final String number);
  void adjust(final int salary);
  void hire(final String number, final int salary);

  public static final class EmployeeHired extends DomainEvent { }
  public static final class EmployeeSalaryAdjusted extends DomainEvent { }
  public static final class EmployeeNumberAssigned extends DomainEvent { }
}
