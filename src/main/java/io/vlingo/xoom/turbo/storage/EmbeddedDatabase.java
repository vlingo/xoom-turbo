// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.storage;

import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.distribution.Version;

public class EmbeddedDatabase {

    private static EmbeddedMysql mysql;
    private static final EmbeddedMysql.Builder mySQLBuilder =
            EmbeddedMysql.anEmbeddedMysql(MysqldConfig.aMysqldConfig(Version.v5_7_latest)
                    .withPort(2215).withUser("xoom_test", "vlingo123").build())
                    .addSchema("STORAGE_TEST");


    public static void main(final String [] args) {
        start(DatabaseType.MYSQL);
    }

    public static void start(final DatabaseType databaseType) {
        if(!databaseType.equals(DatabaseType.MYSQL)) {
            throw new IllegalArgumentException("Only MySQL embedded database is supported");
        }
        mysql = mySQLBuilder.start();
    }

    public static void stop() {
        if(mysql != null) {
            mysql.stop();
        }
    }
}

