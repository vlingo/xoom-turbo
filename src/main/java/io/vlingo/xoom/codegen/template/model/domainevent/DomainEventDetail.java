// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.model.domainevent;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;

public class DomainEventDetail {

  public static boolean hasField(final CodeGenerationParameter domainEvent, final String fieldName) {
    return domainEvent.retrieveAllRelated(Label.STATE_FIELD).anyMatch(field -> field.value.equals(fieldName));
  }

}
