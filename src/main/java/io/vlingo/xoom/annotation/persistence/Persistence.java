// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.persistence;

public @interface Persistence {

    String basePackage();
    StorageType storageType();
    boolean cqrs() default false;

    enum StorageType {
        NONE, STATE_STORE, OBJECT_STORE, JOURNAL;

        public boolean isStateStore() {
            return equals(STATE_STORE);
        }

        public boolean isJournal() {
            return equals(JOURNAL);
        }

        public boolean isObjectStore() {
            return equals(OBJECT_STORE);
        }
    }

}
