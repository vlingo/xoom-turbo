// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.codegen.autodispatch;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.turbo.annotation.codegen.Label;

public class FieldDetail {

  private static String UNKNOWN_FIELD_MESSAGE = "%s is not a field in %s state";

  @SuppressWarnings("static-access")
  public static String typeOf(final CodeGenerationParameter parent, final String fieldName) {
    return parent.retrieveAllRelated(resolveFieldTypeLabel(parent))
            .filter(stateField -> stateField.value.equals(fieldName))
            .map(stateField -> stateField.retrieveRelatedValue(Label.FIELD_TYPE)).findFirst()
            .orElseThrow(() -> new IllegalArgumentException(UNKNOWN_FIELD_MESSAGE.format(fieldName, parent.value)));
  }

  private static Label resolveFieldTypeLabel(final CodeGenerationParameter parent) {
    if (parent.isLabeled(Label.AGGREGATE)) {
      return Label.STATE_FIELD;
    }
    if (parent.isLabeled(Label.VALUE_OBJECT)) {
      return Label.VALUE_OBJECT_FIELD;
    }
    throw new IllegalArgumentException("Unable to resolve field type of " + parent.label);
  }
}
