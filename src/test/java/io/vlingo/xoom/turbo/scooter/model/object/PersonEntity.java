// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.scooter.model.object;

import io.vlingo.xoom.lattice.model.DomainEvent;

public class PersonEntity extends ObjectEntity<PersonState,DomainEvent> implements Person {
  private PersonState person;

  public PersonEntity() {
    this.person = new PersonState(); // unidentified
  }

  public PersonEntity(final long id) {
    this.person = new PersonState(id, "", 0); // recover
  }

  @Override
  public void identify(final String name, final int age) {
    apply(new PersonState(name, age));
  }

  @Override
  public void change(String name) {
    apply(person.with(name));
  }

  @Override
  public void increaseAge() {
    apply(person.with(person.age + 1));
  }

  @Override
  public String id() {
    return String.valueOf(person.persistenceId());
  }

  @Override
  protected void stateObject(final PersonState stateObject) {
    this.person = stateObject;
  }
}
