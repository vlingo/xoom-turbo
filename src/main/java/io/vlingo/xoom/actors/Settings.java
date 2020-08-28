// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.actors;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

public class Settings {

    private static final Properties PROPERTIES =  new Properties();
    private static final String PROPERTIES_FILENAME = "vlingo-xoom.properties";
    private static final String[] RELATIVE_PATH = new String[]{"src", "main", "resources", PROPERTIES_FILENAME};

    static {
        loadProperties();
    }

    public static void loadPropertiesExternally() {
        try {
            final String rootFolder = System.getProperty("user.dir");
            final Path path = Paths.get(rootFolder, RELATIVE_PATH);
            loadProperties(new FileInputStream(path.toFile()));
        } catch (FileNotFoundException e) {
            throw new PropertiesLoadingException(e.getMessage(), e);
        }
    }

    private static void loadProperties() {
        loadProperties(Settings.class.getResourceAsStream("/" + PROPERTIES_FILENAME));
    }

    private static void loadProperties(final InputStream inputStream) {
        try {
            if(inputStream == null) {
                System.out.println("Unable to read vlingo-xoom.properties");
                return;
            }
            PROPERTIES.clear();
            PROPERTIES.load(inputStream);
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