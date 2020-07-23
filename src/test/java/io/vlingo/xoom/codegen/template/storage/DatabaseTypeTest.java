// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.storage;

import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.file.ImportParameter;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.storage.DatabaseType.*;
import static io.vlingo.xoom.codegen.template.storage.ModelClassification.COMMAND;
import static io.vlingo.xoom.codegen.template.storage.ModelClassification.QUERY;
import static io.vlingo.xoom.codegen.template.storage.StorageType.STATE_STORE;

public class DatabaseTypeTest {

    @Test
    public void testParametersEnrichmentOnProductionDatabases() {

        final TemplateParameters postgresParametersOnQueryModel =
                POSTGRES.addConfigurationParameters(TemplateParameters.with(STORAGE_TYPE, STATE_STORE)
                        .and(MODEL_CLASSIFICATION, QUERY));

        Assert.assertEquals("PostgresStorageDelegate", postgresParametersOnQueryModel.find(STORAGE_DELEGATE_NAME));
        Assert.assertEquals("io.vlingo.symbio.store.state.jdbc.postgres.PostgresStorageDelegate", postgresParametersOnQueryModel.<List<ImportParameter>>find(IMPORTS).get(0).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.symbio.store.common.jdbc.postgres.PostgresConfigurationProvider", postgresParametersOnQueryModel.<List<ImportParameter>>find(IMPORTS).get(1).getQualifiedClassName());

        final TemplateParameters postgresParametersOnCommandModel =
                POSTGRES.addConfigurationParameters(TemplateParameters.with(STORAGE_TYPE, STATE_STORE)
                        .and(MODEL_CLASSIFICATION, COMMAND));

        Assert.assertEquals("PostgresStorageDelegate", postgresParametersOnCommandModel.find(STORAGE_DELEGATE_NAME));
        Assert.assertEquals("io.vlingo.symbio.store.state.jdbc.postgres.PostgresStorageDelegate", postgresParametersOnCommandModel.<List<ImportParameter>>find(IMPORTS).get(0).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.symbio.store.common.jdbc.postgres.PostgresConfigurationProvider", postgresParametersOnQueryModel.<List<ImportParameter>>find(IMPORTS).get(1).getQualifiedClassName());

        final TemplateParameters mySqlParameters =
                MYSQL.addConfigurationParameters(TemplateParameters.with(STORAGE_TYPE, STATE_STORE)
                        .and(MODEL_CLASSIFICATION, QUERY));

        Assert.assertEquals("MySQLStorageDelegate", mySqlParameters.find(STORAGE_DELEGATE_NAME));
        Assert.assertEquals("io.vlingo.symbio.store.state.jdbc.mysql.MySQLStorageDelegate", mySqlParameters.<List<ImportParameter>>find(IMPORTS).get(0).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.symbio.store.common.jdbc.mysql.MySQLConfigurationProvider", mySqlParameters.<List<ImportParameter>>find(IMPORTS).get(1).getQualifiedClassName());

        final TemplateParameters hsqldbParameters =
                HSQLDB.addConfigurationParameters(TemplateParameters.with(STORAGE_TYPE, STATE_STORE)
                        .and(MODEL_CLASSIFICATION, QUERY));

        Assert.assertEquals("HSQLDBStorageDelegate", hsqldbParameters.find(STORAGE_DELEGATE_NAME));
        Assert.assertEquals("io.vlingo.symbio.store.state.jdbc.hsqldb.HSQLDBStorageDelegate", hsqldbParameters.<List<ImportParameter>>find(IMPORTS).get(0).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.symbio.store.common.jdbc.hsqldb.HSQLDBConfigurationProvider", hsqldbParameters.<List<ImportParameter>>find(IMPORTS).get(1).getQualifiedClassName());

        final TemplateParameters yugaByteParameters =
                YUGA_BYTE.addConfigurationParameters(TemplateParameters.with(STORAGE_TYPE, STATE_STORE)
                        .and(MODEL_CLASSIFICATION, QUERY));

        Assert.assertEquals("YugaByteStorageDelegate", yugaByteParameters.find(STORAGE_DELEGATE_NAME));
        Assert.assertEquals("io.vlingo.symbio.store.state.jdbc.yugabyte.YugaByteStorageDelegate", yugaByteParameters.<List<ImportParameter>>find(IMPORTS).get(0).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.symbio.store.common.jdbc.yugabyte.YugaByteConfigurationProvider", yugaByteParameters.<List<ImportParameter>>find(IMPORTS).get(1).getQualifiedClassName());
    }

}
