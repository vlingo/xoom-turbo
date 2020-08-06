// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.storage;

public class DatabaseParameter {

    private final DatabaseType databaseType;

    public DatabaseParameter(final DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    public String getName() {
        return databaseType.name().toUpperCase();
    }

    public String getUrl() {
        return databaseType.connectionUrl;
    }

    public String getDriver() {
        return databaseType.driver;
    }

}
