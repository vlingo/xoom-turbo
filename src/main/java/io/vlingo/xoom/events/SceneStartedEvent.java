// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.events;

import io.micronaut.context.event.ApplicationEvent;
import io.vlingo.xoom.server.VlingoScene;

public class SceneStartedEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;

    private final VlingoScene source;

    public SceneStartedEvent(VlingoScene source) {
        super(source);
        this.source = source;
    }

    @Override
    public VlingoScene getSource() {
        return source;
    }
}
