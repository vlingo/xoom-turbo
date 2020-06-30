// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static io.vlingo.xoom.annotation.initializer.Xoom.StorageType.NONE;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Xoom {

    String name();

    AddressFactory addressFactory();

    StorageType storageType() default NONE;

    boolean CQRS() default false;

    enum StorageType {
        NONE, JOURNAL, STATE_STORE, OBJECT_STORE;
    }

}
