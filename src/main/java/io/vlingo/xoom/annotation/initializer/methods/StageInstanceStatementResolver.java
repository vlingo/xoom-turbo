// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.initializer.methods;

import io.vlingo.actors.Stage;
import io.vlingo.common.identity.IdentityGeneratorType;
import io.vlingo.xoom.annotation.initializer.AddressFactory;
import io.vlingo.xoom.annotation.initializer.Xoom;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import static io.vlingo.xoom.annotation.initializer.XoomInitializerStatements.BASIC_STAGE_INSTANCE_STATEMENT;
import static io.vlingo.xoom.annotation.initializer.XoomInitializerStatements.STAGE_INSTANCE_STATEMENT;

public class StageInstanceStatementResolver {

    public static Entry<String, Object[]> resolve(final Xoom xoomAnnotation) {
        if(xoomAnnotation.addressFactory().type().isBasic()) {
            return new SimpleEntry(BASIC_STAGE_INSTANCE_STATEMENT,
                    new Object[]{Stage.class, xoomAnnotation.name()});
        }

        final AddressFactory.IdentityGenerator generator =
                xoomAnnotation.addressFactory().generator();

        final IdentityGeneratorType identityGeneratorType =
                generator.resolveWith(xoomAnnotation.addressFactory().type());

        return new SimpleEntry(STAGE_INSTANCE_STATEMENT,
                new Object[]{Stage.class, xoomAnnotation.name(), Stage.class,
                        xoomAnnotation.addressFactory().type().clazz,
                        IdentityGeneratorType.class, identityGeneratorType});
    }

}
