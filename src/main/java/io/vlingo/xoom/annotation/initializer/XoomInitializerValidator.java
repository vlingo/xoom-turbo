// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.initializer;

import javax.lang.model.element.Element;
import java.util.Arrays;
import java.util.Set;

import static io.vlingo.xoom.annotation.initializer.XoomInitializerValidation.*;

public class XoomInitializerValidator {

    private static XoomInitializerValidator instance;

    private XoomInitializerValidator() {}

    public static XoomInitializerValidator instance() {
        if(instance == null) {
            instance = new XoomInitializerValidator();
        }
        return instance;
    }

    public void validate(final Set<? extends Element> annotatedElements) {
        Arrays.asList(singularityValidation(), targetValidation(), classVisibilityValidation())
                .forEach(validator -> validator.validate(annotatedElements));
    }

}
