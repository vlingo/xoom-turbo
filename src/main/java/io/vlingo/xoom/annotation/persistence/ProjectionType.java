// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.persistence;

public enum ProjectionType {

    NONE,
    EVENT_BASED,
    OPERATION_BASED;

    public boolean isEventBased() {
        return equals(EVENT_BASED);
    }

    public boolean isOperationBased() {
        return equals(OPERATION_BASED);
    }

}
