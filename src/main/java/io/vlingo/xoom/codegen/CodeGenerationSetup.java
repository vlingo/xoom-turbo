// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen;

import com.google.common.collect.Maps;
import io.vlingo.xoom.codegen.template.projections.ProjectionType;
import io.vlingo.xoom.codegen.template.storage.DatabaseType;
import io.vlingo.xoom.codegen.template.storage.ModelClassification;
import io.vlingo.xoom.codegen.template.storage.StorageType;

import java.util.HashMap;
import java.util.Map;

import static io.vlingo.xoom.codegen.CodeGenerationParameter.*;
import static io.vlingo.xoom.codegen.template.Template.*;

public class CodeGenerationSetup {

    public static final Map<StorageType, String> AGGREGATE_TEMPLATES =
            Maps.immutableEnumMap(
                    new HashMap<StorageType, String>(){{
                        put(StorageType.OBJECT_STORE, OBJECT_ENTITY.filename);
                        put(StorageType.STATE_STORE, STATEFUL_ENTITY.filename);
                        put(StorageType.JOURNAL, EVENT_SOURCE_ENTITY.filename);
                    }}
            );

    public static final Map<StorageType, String> STATE_TEMPLATES =
            Maps.immutableEnumMap(
                    new HashMap<StorageType, String>(){{
                        put(StorageType.OBJECT_STORE, STATE_OBJECT.filename);
                        put(StorageType.STATE_STORE, STATE_OBJECT.filename);
                        put(StorageType.JOURNAL, PLAIN_STATE.filename);
                    }}
            );

    public static final Map<StorageType, String> ADAPTER_TEMPLATES =
            Maps.immutableEnumMap(
                    new HashMap<StorageType, String>(){{
                        put(StorageType.OBJECT_STORE, "");
                        put(StorageType.STATE_STORE, STATE_ADAPTER.filename);
                        put(StorageType.JOURNAL, ENTRY_ADAPTER.filename);
                    }}
            );

    public static final Map<ProjectionType, String> PROJECTION_TEMPLATES =
            Maps.immutableEnumMap(
                    new HashMap<ProjectionType, String>(){{
                        put(ProjectionType.EVENT_BASED, EVENT_BASED_PROJECTION.filename);
                        put(ProjectionType.OPERATION_BASED, OPERATION_BASED_PROJECTION.filename);
                    }}
            );

    public static final String TEST_CLASSES_FOLDER_NAME = "test-classes";

    public static final String STORAGE_DELEGATE_QUALIFIED_NAME_PATTERN = "io.vlingo.symbio.store.state.jdbc.%s.%s";

    public static final Map<DatabaseType, String> STORAGE_DELEGATE_CLASS_NAME =
            Maps.immutableEnumMap(
                    new HashMap<DatabaseType, String>(){{
                        put(DatabaseType.POSTGRES, "PostgresStorageDelegate");
                        put(DatabaseType.MYSQL, "MySQLStorageDelegate");
                        put(DatabaseType.HSQLDB, "HSQLDBStorageDelegate");
                        put(DatabaseType.YUGA_BYTE, "YugaByteStorageDelegate");
                    }}
            );

    private static final Map<StorageType, String> COMMAND_MODEL_STORE_TEMPLATES =
            Maps.immutableEnumMap(
                    new HashMap<StorageType, String>(){{
                        put(StorageType.OBJECT_STORE, OBJECT_STORE_PROVIDER.filename);
                        put(StorageType.STATE_STORE, STATE_STORE_PROVIDER.filename);
                        put(StorageType.JOURNAL, JOURNAL_PROVIDER.filename);
                    }}
            );

    private static final Map<StorageType, String> QUERY_MODEL_STORE_TEMPLATES =
            Maps.immutableEnumMap(
                    new HashMap<StorageType, String>(){{
                        put(StorageType.OBJECT_STORE, STATE_STORE_PROVIDER.filename);
                        put(StorageType.STATE_STORE, STATE_STORE_PROVIDER.filename);
                        put(StorageType.JOURNAL, STATE_STORE_PROVIDER.filename);
                    }}
            );

    public static Map<StorageType, String> storeProviderTemplatesFrom(final ModelClassification modelClassification) {
        if(modelClassification.isQueryModel()) {
            return QUERY_MODEL_STORE_TEMPLATES;
        }
        return COMMAND_MODEL_STORE_TEMPLATES;
    }

}
