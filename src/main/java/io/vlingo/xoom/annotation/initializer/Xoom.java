// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer;

import io.vlingo.actors.AddressFactory;
import io.vlingo.actors.GridAddressFactory;
import io.vlingo.actors.UUIDAddressFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static io.vlingo.xoom.annotation.initializer.Xoom.AddressFactoryType.BASIC;
import static io.vlingo.xoom.annotation.initializer.Xoom.StorageType.NONE;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Xoom {

    String name();

    AddressFactoryType addressFactory() default BASIC;

    StorageType storageType() default NONE;

    boolean CQRS() default false;

    enum StorageType {
        NONE, JOURNAL, STATE_STORE, OBJECT_STORE;
    }

    enum AddressFactoryType {
        BASIC(AddressFactory.class),
        UUID(UUIDAddressFactory.class),
        GRID(GridAddressFactory.class);

        public final Class<?> clazz;

        AddressFactoryType(final Class<?> clazz) {
            this.clazz = clazz;
        }

        boolean isBasic() {
            return equals(BASIC);
        }

    }

}
