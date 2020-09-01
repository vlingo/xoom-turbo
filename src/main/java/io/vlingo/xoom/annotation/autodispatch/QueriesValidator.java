// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.autodispatch;

import io.vlingo.xoom.annotation.AnnotatedElements;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.Arrays;

import static io.vlingo.xoom.annotation.Validation.classVisibilityValidation;
import static io.vlingo.xoom.annotation.Validation.targetValidation;

public class QueriesValidator {

    private static QueriesValidator instance;

    private QueriesValidator() {}

    public static QueriesValidator instance() {
        if(instance == null) {
            instance = new QueriesValidator();
        }
        return instance;
    }

    public void validate(final ProcessingEnvironment environment, final AnnotatedElements annotatedElements) {
        Arrays.asList(
                targetValidation(),
                classVisibilityValidation(),
                new ActorsValidation())
                .forEach(validator ->
                        validator.validate(environment, Queries.class, annotatedElements));
    }

}
