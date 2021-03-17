// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.initializer;

import io.vlingo.xoom.annotation.AnnotatedElements;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.Arrays;

import static io.vlingo.xoom.annotation.Validation.*;

public class XoomValidator {

    private static XoomValidator instance;

    private XoomValidator() {}

    public static XoomValidator instance() {
        if(instance == null) {
            instance = new XoomValidator();
        }
        return instance;
    }

    public void validate(final ProcessingEnvironment environment, final AnnotatedElements annotatedElements) {
        Arrays.asList(singularityValidation(), targetValidation(), classVisibilityValidation())
                .forEach(validator -> validator.validate(environment, Xoom.class, annotatedElements));
    }

}
