// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom;

import io.vlingo.common.Completes;

import java.util.List;

public interface TestDeliveryProtocol {
    void reactTo();
    void reactTo(final int x, final int y, final int z);
    void reactTo(final String text);
    Completes<List<String>> reactions();
}
