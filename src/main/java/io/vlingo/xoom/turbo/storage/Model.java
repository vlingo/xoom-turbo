// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.storage;

public enum Model {

    DOMAIN("Domain"),
    COMMAND("Command"),
    QUERY("Query");

    private final String label;

    Model(final String label) {
        this.label = label;
    }

    public boolean isQueryModel() {
        return equals(QUERY);
    }

    public boolean isDomainModel() {
        return equals(DOMAIN);
    }

    @Override
    public String toString() {
        return label;
    }

}
