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

public class Settings {

    private static final Properties PROPERTIES = new Properties();
    private static final String PROPERTIES_FILENAME = "/vlingo-xoom.properties";
    private static final Map<Object, Object> DEFAULT_DATABASE_PROPERTIES = new HashMap<Object, Object>() {
      {
        put("database", "IN_MEMORY");
        put("query.database", "IN_MEMORY");
      }
    };

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try {
            final InputStream stream =
                    Settings.class.getResourceAsStream(PROPERTIES_FILENAME);

            if(stream == null) {
                System.out.println("Unable to read properties. VLINGO/XOOM will set the default mailbox and logger");
                PROPERTIES.putAll(DEFAULT_DATABASE_PROPERTIES);
            } else {
                PROPERTIES.load(stream);
            }
        } catch (final IOException e) {
            throw new PropertiesLoadingException(e.getMessage(), e);
        }
    }

    public static Properties properties() {
        return PROPERTIES;
    }

}