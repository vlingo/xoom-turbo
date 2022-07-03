// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo;

import io.vlingo.xoom.actors.Configuration;
import io.vlingo.xoom.cluster.model.Properties;
import io.vlingo.xoom.lattice.grid.Grid;
import io.vlingo.xoom.lattice.grid.GridClusterProperties;
import io.vlingo.xoom.turbo.actors.Settings;

public class Boot {

  private static String resolvedNodeName;
  private static Properties clusterProperties;

  public static Grid start(final String worldName) throws Exception {
    return start(worldName, null);
  }

  public static Grid start(final String worldName, final String nodeName) throws Exception {
    return start(worldName, nodeName, null);
  }

  public static Grid start(final String worldName, final String nodeName, final Properties customProperties) throws Exception {
    if (customProperties == null) {
      System.out.println("Unable to find xoom-cluster.properties. Using default grid cluster settings.");
      clusterProperties = GridClusterProperties.oneNode();
    } else {
      clusterProperties = customProperties;
    }

    resolvedNodeName = resolveNodeName(nodeName);
    return Grid.start(worldName, Configuration.define(), clusterProperties, resolvedNodeName);
  }

  public static int serverPort() {
    return Settings.serverPort();
  }

  private static String resolveNodeName(final String nodeName) {
    if (nodeName == null || nodeName.isEmpty()) {
      return clusterProperties.nodes().get(0);
    }
    return nodeName;
  }
}