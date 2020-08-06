// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.storage;

import io.vlingo.symbio.store.common.jdbc.Configuration;

public class DatabaseConfiguration {

    private final Model model;
    private final boolean autoCreate;

    public static Configuration load(final Model model) {
        return load(model, true);
    }

    public static Configuration load(final Model model, final boolean autoCreate) {
        return new DatabaseConfiguration(model, autoCreate).load();
    }

    private DatabaseConfiguration(final Model model, final boolean autoCreate) {
        this.model = model;
        this.autoCreate = autoCreate;
    }

    private Configuration load() {
        return new DatabaseParameters(model).mapToConfiguration();
    }

}
