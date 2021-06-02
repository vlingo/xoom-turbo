// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.actors;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Settings {

  private static final int DEFAULT_PORT = 18080;
  private static final Properties PROPERTIES = new Properties();
  private static final String PROPERTIES_FILENAME = "/xoom-turbo.properties";
  private static final Map<Object, Object> DEFAULT_DATABASE_PROPERTIES = new HashMap<Object, Object>() {
    private static final long serialVersionUID = 1L;

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

      if (stream == null) {
        System.out.println("Unable to read properties. VLINGO XOOM Turbo will set the default mailbox and logger");
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

  public static int serverPort() {
    final String serverPort =
            Settings.properties().getOrDefault("xoom.http.server.port", DEFAULT_PORT).toString();

    return Integer.valueOf(serverPort);
  }

  public static void clear() {
    PROPERTIES.clear();
  }
}