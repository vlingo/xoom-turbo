// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.model;

import io.vlingo.actors.Definition;
import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;

import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.FACTORY_METHOD;

public enum MethodScope {

    INSTANCE(Completes.class),
    STATIC(Completes.class, Definition.class, Stage.class);

    public final Class<?>[] requiredClasses;

    MethodScope(final Class<?> ...requiredClasses) {
        this.requiredClasses = requiredClasses;
    }

    public boolean isStatic() {
        return equals(STATIC);
    }

    public boolean isInstance() {
        return equals(INSTANCE);
    }

    public static Stream<MethodScope> infer(final CodeGenerationParameter method) {
        if(method.retrieveRelatedValue(FACTORY_METHOD, Boolean::valueOf)) {
            return Stream.of(values());
        }
        return Stream.of(INSTANCE);
    }


}
