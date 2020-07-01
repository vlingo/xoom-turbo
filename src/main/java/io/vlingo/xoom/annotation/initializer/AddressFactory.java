// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.initializer;

import io.vlingo.actors.GridAddressFactory;
import io.vlingo.actors.UUIDAddressFactory;
import io.vlingo.common.identity.IdentityGeneratorType;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface AddressFactory {

    Type type();

    IdentityGenerator generator() default IdentityGenerator.DEFAULT;

    enum Type {
        BASIC(AddressFactory.class),
        UUID(UUIDAddressFactory.class),
        GRID(GridAddressFactory.class);

        public final Class<?> clazz;

        Type(final Class<?> clazz) {
            this.clazz = clazz;
        }

        boolean isBasic() {
            return equals(BASIC);
        }
    }

    enum IdentityGenerator {
        DEFAULT(null),
        TIME_BASED(IdentityGeneratorType.TIME_BASED),
        NAME_BASED(IdentityGeneratorType.NAME_BASED),
        RANDOM(IdentityGeneratorType.RANDOM);

        public final IdentityGeneratorType generatorType;

        IdentityGenerator(final IdentityGeneratorType generatorType) {
            this.generatorType = generatorType;
        }

        public IdentityGeneratorType resolveWith(final AddressFactory.Type addressFactoryType) {
            if(addressFactoryType.isBasic()) {
                return DEFAULT.generatorType;
            }
            if(isDefault()) {
                return RANDOM.generatorType;
            }
            return this.generatorType;
        }

        public boolean isDefault() {
            return equals(DEFAULT);
        }

    }



}
