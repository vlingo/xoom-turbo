// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.stepflow;

import io.vlingo.xoom.symbio.Source;

public abstract class Event extends Source<Event> implements Transition {

    private String sourceName = "";
    private String targetName = "";

    public Event() {
    }

    public Event(String source, String target) {
        this.sourceName = source;
        this.targetName = target;
    }

    @Override
    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getEventType() {
        return getSourceName() + "::" + getTargetName();
    }
}
