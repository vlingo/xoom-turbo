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

public class Settings {

    private static final Properties PROPERTIES;
    private static final String PROPERTIES_FILE_PATH = "/vlingo-xoom.properties";
    private static final Map<String, Object> BLOCKING_MAILBOX_PROPERTIES = new HashMap<>() {{
        put("plugin.name.blockingMailbox", true);
        put("plugin.blockingMailbox.classname", "io.vlingo.xoom.scooter.plugin.mailbox.blocking.BlockingMailboxPlugin");
        put("plugin.blockingMailbox.defaultMailbox", true);
    }};

    static {
        try {
            PROPERTIES = new java.util.Properties();
            PROPERTIES.load(Settings.class.getResourceAsStream(PROPERTIES_FILE_PATH));
        } catch (final IOException e) {
            throw new PropertiesLoadingException(e.getMessage(), e);
        }
    }

    public static void enableBlockingMailbox() {
        PROPERTIES.putAll(BLOCKING_MAILBOX_PROPERTIES);
    }

    public static void disableBlockingMailbox() {
        BLOCKING_MAILBOX_PROPERTIES.keySet().forEach(key -> PROPERTIES.remove(key));
    }

    public static Properties properties() {
        return PROPERTIES;
    }

}
