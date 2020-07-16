// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.persistence;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

public class PersistenceConfigurationPackage {

    public static String from(final ProcessingEnvironment environment,
                              final Element persistenceClass) {

        return environment.getElementUtils()
                .getPackageOf(persistenceClass)
                .toString();
    }

}
