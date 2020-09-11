// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.actors;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

public class Settings {

    private static final Properties PROPERTIES = new Properties();

    private static final String PROPERTIES_FILENAME = "/vlingo-xoom.properties";

    private static final Map<Object, Object> COMPLETES_PLUGIN_PROPERTIES = new HashMap<Object, Object>() {{
        put("plugin.name.pooledCompletes", "true");
        put("plugin.pooledCompletes.classname", "io.vlingo.actors.plugin.completes.PooledCompletesPlugin");
        put("plugin.pooledCompletes.pool", "10");
        put("plugin.pooledCompletes.mailbox", "queueMailbox");
    }};

    private static final Map<Object, Object> LOGGING_PROPERTIES = new HashMap<Object, Object>() {{
        put("plugin.name.slf4jLogger", "true");
        put("plugin.slf4jLogger.classname", "io.vlingo.actors.plugin.logging.slf4j.Slf4jLoggerPlugin");
        put("plugin.slf4jLogger.name", "vlingo/xoom");
        put("plugin.slf4jLogger.defaultLogger", "true");
    }};

    private static final Map<Object, Object> BLOCKING_MAILBOX_PROPERTIES = new HashMap<Object, Object>() {{
        put("plugin.name.blockingMailbox", "true");
        put("plugin.blockingMailbox.classname", "io.vlingo.xoom.scooter.plugin.mailbox.blocking.BlockingMailboxPlugin");
        put("plugin.blockingMailbox.defaultMailbox", "true");
    }};

    private static final Map<Object, Object> DEFAULT_MAILBOX_PROPERTIES = new HashMap<Object, Object>() {{
        put("plugin.name.queueMailbox", "true");
        put("plugin.queueMailbox.classname", "io.vlingo.actors.plugin.mailbox.concurrentqueue.ConcurrentQueueMailboxPlugin");
        put("plugin.queueMailbox.defaultMailbox", "true");
        put("plugin.queueMailbox.numberOfDispatchersFactor", 1.5);
        put("plugin.queueMailbox.numberOfDispatchers", 0);
        put("plugin.queueMailbox.dispatcherThrottlingCount", 1);
    }};

    private static final Map<Object, Object> DATABASE_PROPERTIES = new HashMap<Object, Object>() {{
        put("database", "IN_MEMORY");
        put("query.database", "IN_MEMORY");
    }};

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try {
            final InputStream stream =
                    Settings.class.getResourceAsStream(PROPERTIES_FILENAME);

            if(stream == null) {
                System.out.println("Unable to read properties. VLINGO/XOOM will set the default mailbox and logger");
                PROPERTIES.putAll(COMPLETES_PLUGIN_PROPERTIES);
                PROPERTIES.putAll(LOGGING_PROPERTIES);
                PROPERTIES.putAll(DATABASE_PROPERTIES);
                disableBlockingMailbox();
            } else {
                PROPERTIES.load(stream);
            }
        } catch (final IOException e) {
            throw new PropertiesLoadingException(e.getMessage(), e);
        }
    }

    public static void enableBlockingMailbox() {
        patchMailboxProperties(BLOCKING_MAILBOX_PROPERTIES);
    }

    public static void disableBlockingMailbox() {
        patchMailboxProperties(DEFAULT_MAILBOX_PROPERTIES);
    }

    private static void patchMailboxProperties(final Map<Object, Object> mailboxProperties) {
        Stream.of(BLOCKING_MAILBOX_PROPERTIES, DEFAULT_MAILBOX_PROPERTIES)
                .flatMap(map -> map.entrySet().stream())
                .forEach(entry -> PROPERTIES.remove(entry.getKey()));

        PROPERTIES.putAll(mailboxProperties);
    }

    public static Properties properties() {
        return PROPERTIES;
    }

}