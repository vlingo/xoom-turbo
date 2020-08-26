// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.persistence;

import io.vlingo.xoom.annotation.AnnotatedElements;

import javax.lang.model.element.Element;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static io.vlingo.xoom.annotation.Validation.*;

public class PersistenceValidator {

    private static PersistenceValidator instance;

    private PersistenceValidator() {}

    public static PersistenceValidator instance() {
        if(instance == null) {
            instance = new PersistenceValidator();
        }
        return instance;
    }

    public void validate(final AnnotatedElements annotatedElements) {
        Arrays.asList(singularityValidation(), targetValidation(), classVisibilityValidation())
                .forEach(validator -> validator.validate(Persistence.class, annotatedElements));
    }

}
