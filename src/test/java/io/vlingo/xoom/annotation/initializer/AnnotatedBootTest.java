// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer;

import io.vlingo.actors.Stage;
import io.vlingo.xoom.XoomInitializationAware;

/**
 * This test class ensures that auto-generated code, from
 * the {@link Xoom} compile-time annotation, is successfully
 * compiled.
 *
 * @author Danilo Ambrosio
 */
@Xoom(name = "annotated-boot")
@ResourceHandlers({FirstResource.class, SecondResource.class})
public class AnnotatedBootTest implements XoomInitializationAware {

    @Override
    public void onInit(final Stage stage) {
        //To be invoked after Stage initialization...
    }

}