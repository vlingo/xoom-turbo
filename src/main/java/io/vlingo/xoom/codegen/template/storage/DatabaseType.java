// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.storage;

import io.vlingo.xoom.codegen.CodeGenerationSetup;
import io.vlingo.xoom.codegen.template.TemplateParameters;

import static io.vlingo.xoom.codegen.CodeGenerationSetup.STORAGE_DELEGATE_QUALIFIED_NAME_PATTERN;
import static io.vlingo.xoom.codegen.template.TemplateParameter.*;

public enum DatabaseType {

    IN_MEMORY("in_memory"),
    POSTGRES("postgres", "org.postgresql.Driver", "jdbc:postgresql://localhost/"),
    HSQLDB("hsqldb", "org.hsqldb.jdbc.JDBCDriver", "jdbc:hsqldb:mem:"),
    MYSQL("mysql", "com.mysql.cj.jdbc.Driver", "jdbc:mysql://localhost/"),
    YUGA_BYTE("yugabyte", "org.postgresql.Driver", "jdbc:postgresql://localhost/");

    public final String label;
    public final String driver;
    public final String connectionUrl;
    public final boolean configurable;

    DatabaseType(final String label) {
        this(label, "", "", false);
    }

    DatabaseType(final String label,
                 final String driver,
                 final String connectionUrl) {
        this(label, driver, connectionUrl, true);
    }

    DatabaseType(final String label,
                 final String driver,
                 final String connectionUrl,
                 final boolean configurable) {
        this.label = label;
        this.driver = driver;
        this.connectionUrl = connectionUrl;
        this.configurable = configurable;
    }

    public static DatabaseType getOrDefault(final String name, final DatabaseType defaultDatabase) {
        if(name == null) {
            return defaultDatabase;
        }
        return valueOf(name);
    }

}
