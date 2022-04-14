// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.autodispatch;

import io.vlingo.xoom.turbo.annotation.AnnotatedElements;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.Arrays;

import static io.vlingo.xoom.turbo.annotation.Validation.classVisibilityValidation;
import static io.vlingo.xoom.turbo.annotation.Validation.isInterface;
import static io.vlingo.xoom.turbo.annotation.autodispatch.AutoDispatchValidations.*;

public class AutoDispatchValidator {

  private static AutoDispatchValidator instance;

  private AutoDispatchValidator() {
  }

  public static AutoDispatchValidator instance() {
    if (instance == null) {
      instance = new AutoDispatchValidator();
    }
    return instance;
  }

  public void validate(final ProcessingEnvironment processingEnvironment, final AnnotatedElements annotatedElements) {
    Arrays.asList(isInterface(), classVisibilityValidation(), isQueriesProtocolAnInterface(),
            queryWithoutModelValidator(), bodyForRouteValidator(), hasAutoDispatchAnnotation())
            .forEach(validator ->
                    validator.validate(processingEnvironment, Queries.class, annotatedElements));

    Arrays.asList(isInterface(), classVisibilityValidation(), isProtocolModelAnInterface(),
            modelWithoutQueryValidator(), routeWithoutResponseValidator(), handlerWithoutValidMethodValidator(),
            hasAutoDispatchAnnotation(), entityActorValidation())
            .forEach(validator ->
                    validator.validate(processingEnvironment, Model.class, annotatedElements));

    Arrays.asList(routeHasQueryOrModel())
            .forEach(validator ->
                    validator.validate(processingEnvironment, Route.class, annotatedElements));

    Arrays.asList(handlerTypeValidation())
            .forEach(validator ->
                    validator.validate(processingEnvironment, AutoDispatch.class, annotatedElements));

  }

}


