// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.annotation.initializer;

import io.vlingo.xoom.turbo.annotation.AnnotatedElements;

import javax.annotation.processing.ProcessingEnvironment;

public class XoomInitializerPackage {

    public static String from(final ProcessingEnvironment environment,
                              final AnnotatedElements elements) {
        return environment.getElementUtils().getPackageOf(elements.elementWith(Xoom.class)).toString();
    }

}
