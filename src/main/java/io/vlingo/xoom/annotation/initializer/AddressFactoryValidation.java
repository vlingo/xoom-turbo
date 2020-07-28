// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.initializer;

import io.vlingo.xoom.annotation.ProcessingAnnotationException;
import io.vlingo.xoom.annotation.Validation;

import javax.lang.model.element.Element;
import java.util.Map;
import java.util.Set;

public class AddressFactoryValidation implements Validation {

    @Override
    public void validate(final Class annotation, final Map<Class, Set<Element>> annotatedElements) {
        annotatedElements.get(Xoom.class).forEach(element -> {
            final Xoom xoom = element.getAnnotation(Xoom.class);
            final AddressFactory addressFactory = xoom.addressFactory();
            if(addressFactory.type().isBasic() && !addressFactory.generator().isDefault()) {
                throw new ProcessingAnnotationException("The BasicAddressFactory only supports the Default generator.");
            }
        });
    }
}
