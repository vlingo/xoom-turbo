// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.initializer;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import io.vlingo.actors.World;
import io.vlingo.http.resource.Server;

import java.util.Arrays;
import java.util.List;

import static io.vlingo.xoom.annotation.initializer.XoomInitializerGenerator.XOOM_INITIALIZER_CLASS_NAME;
import static javax.lang.model.element.Modifier.*;

public class XoomInitializerFields {

    public static List<FieldSpec> from(final String basePackage) {
        final ClassName className =
                ClassName.get(basePackage, XOOM_INITIALIZER_CLASS_NAME);

        return Arrays.asList(
                FieldSpec.builder(className.box(), "instance", PRIVATE, STATIC).build(),
                FieldSpec.builder(Integer.class, "DEFAULT_PORT", PRIVATE, STATIC, FINAL).initializer("18080").build(),
                FieldSpec.builder(Server.class, "server", PRIVATE, FINAL).build(),
                FieldSpec.builder(World.class, "world", PRIVATE, FINAL).build());
    }
}
