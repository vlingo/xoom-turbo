// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.annotation.persistence;

import io.vlingo.xoom.turbo.annotation.AnnotatedElements;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.Arrays;

import static io.vlingo.xoom.turbo.annotation.Validation.*;

public class PersistenceValidator {

  private static PersistenceValidator instance;

  private PersistenceValidator() {
  }

  public static PersistenceValidator instance() {
    if (instance == null) {
      instance = new PersistenceValidator();
    }
    return instance;
  }

  public void validate(final ProcessingEnvironment processingEnvironment, final AnnotatedElements annotatedElements) {
    Arrays.asList(singularityValidation(), targetValidation(), classVisibilityValidation())
            .forEach(validator -> validator.validate(processingEnvironment, Persistence.class, annotatedElements));
  }

}
