// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer;

import com.squareup.javapoet.MethodSpec;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.util.Arrays;
import java.util.List;

public class XoomInitializerMethods {

    public static List<MethodSpec> from(final String basePackage,
                                        final ProcessingEnvironment environment,
                                        final Element bootstrapClass) {
        return Arrays.asList(MainMethod.from(bootstrapClass),
                ConstructorMethod.from(basePackage, environment, bootstrapClass),
                OnInitMethod.from(bootstrapClass), InstanceMethod.from(basePackage));
    }

}