// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.bootstrap;

import io.vlingo.actors.GridAddressFactory;
import io.vlingo.actors.UUIDAddressFactory;
import io.vlingo.common.identity.IdentityGeneratorType;
import io.vlingo.xoom.annotation.initializer.AddressFactory;

import java.util.function.BiFunction;

public enum AddressFactoryType {

    BASIC(AddressFactory.class.getCanonicalName(),
            "\"%s\"",
            (appName, identityGeneratorType) -> new String[]{appName}),

    UUID(UUIDAddressFactory.class.getCanonicalName(),
            "\"%s\", Stage.class, new UUIDAddressFactory(IdentityGeneratorType.%s)",
            (appName, identityGeneratorType) -> new String[]{appName, identityGeneratorType.name()}),

    GRID(GridAddressFactory.class.getCanonicalName(),
            "\"%s\", Stage.class, new GridAddressFactory(IdentityGeneratorType.%s)",
            (appName, identityGeneratorType) -> new String[]{appName, identityGeneratorType.name()});

    public final String qualifiedName;
    private final String parametersPattern;
    private final BiFunction<String, IdentityGeneratorType, String[]> parametersReplacer;

    AddressFactoryType(final String qualifiedName,
                       final String parametersPattern,
                       final BiFunction<String, IdentityGeneratorType, String[]> parametersReplacer) {
        this.qualifiedName = qualifiedName;
        this.parametersPattern = parametersPattern;
        this.parametersReplacer = parametersReplacer;
    }

    public String resolveParameters(final String appName, final IdentityGeneratorType generator) {
        return String.format(parametersPattern, this.parametersReplacer.apply(appName, generator));
    }

    public boolean isBasic() {
        return equals(BASIC);
    }
}

