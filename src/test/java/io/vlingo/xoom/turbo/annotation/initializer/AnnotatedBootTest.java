// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.initializer;

import io.vlingo.xoom.actors.Grid;
import io.vlingo.xoom.turbo.XoomInitializationAware;

/**
 * This test class ensures that auto-generated code, from
 * the {@link Xoom} compile-time annotation, is successfully
 * compiled.
 *
 * @author Danilo Ambrosio
 */
@Xoom(name = "annotated-boot")
@ResourceHandlers(packages = "io.vlingo.xoom.turbo.annotation.initializer.resources")
public class AnnotatedBootTest implements XoomInitializationAware {

    @Override
    public void onInit(Grid grid) {

    }
}