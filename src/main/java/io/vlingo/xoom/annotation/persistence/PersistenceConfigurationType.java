// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.persistence;

import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import static io.vlingo.xoom.annotation.persistence.PersistenceConfigurationStatements.PERSISTENCE_CONFIGURATION_CLASSNAME;
import static javax.lang.model.element.Modifier.PUBLIC;

public class PersistenceConfigurationType {

    public static TypeSpec from(final ProcessingEnvironment environment,
                                final String basePackage,
                                final Element annotatedClass) {

        return TypeSpec.classBuilder(PERSISTENCE_CONFIGURATION_CLASSNAME)
                .addModifiers(PUBLIC).addMethods(PersistenceConfigurationMethods.from(
                        basePackage, environment, annotatedClass)).build();
    }

}
