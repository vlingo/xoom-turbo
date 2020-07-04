// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom;

import io.vlingo.actors.Stage;
import io.vlingo.http.resource.Resource;

public abstract class Endpoint {

    protected final Stage stage;

    public Endpoint(final Stage stage) {
        this.stage = stage;
    }

    public abstract Resource<?> routes();
}
