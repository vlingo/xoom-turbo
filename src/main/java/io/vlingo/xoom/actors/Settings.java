// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.actors;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class Settings {

    private static final Properties PROPERTIES =  new Properties();
    private static final String PROPERTIES_FILE_PATH = "/vlingo-xoom.properties";

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try {
            PROPERTIES.load(Settings.class.getResourceAsStream(PROPERTIES_FILE_PATH));
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
        final Map<Object, Object> otherProperties =
                PROPERTIES.entrySet().stream().filter(entry -> !isMailboxProperty(entry.getKey()))
                        .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));

        PROPERTIES.clear();
        PROPERTIES.putAll(mailboxProperties);
        PROPERTIES.putAll(otherProperties);
    }

    private static boolean isMailboxProperty(final Object key) {
        return DEFAULT_MAILBOX_PROPERTIES.containsKey(key) || BLOCKING_MAILBOX_PROPERTIES.containsKey(key);
    }

    public static Properties properties() {
        return PROPERTIES;
    }

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

}