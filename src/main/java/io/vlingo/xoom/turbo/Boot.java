// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo;

import io.vlingo.xoom.actors.Configuration;
import io.vlingo.xoom.cluster.StaticClusterConfiguration;
import io.vlingo.xoom.cluster.model.NodeProperties;
import io.vlingo.xoom.cluster.model.Properties;
import io.vlingo.xoom.lattice.grid.Grid;
import io.vlingo.xoom.lattice.grid.GridClusterProperties;
import io.vlingo.xoom.turbo.actors.Settings;

public class Boot {

  private static String nodeProperties;
  private static Properties clusterProperties;

  public static Grid start(final String worldName) throws Exception {
    return start(worldName, null);
  }

  public static Grid start(final String worldName, final String localNodeProperties) throws Exception {
    return start(worldName, localNodeProperties, null);
  }

  public static Grid start(final String worldName, final String localNodeProperties, final Properties customProperties) throws Exception {
    if (customProperties == null) {
      System.out.println("Unable to find xoom-cluster.properties. Using default grid cluster settings.");
      StaticClusterConfiguration staticConfiguration = GridClusterProperties.oneNode();
      clusterProperties = staticConfiguration.properties;
      nodeProperties = staticConfiguration.propertiesOf(0);
    } else {
      if (localNodeProperties == null || localNodeProperties.isEmpty()) {
        // not allowed because 'cluster.seeds' have to correlate with 'localNodeProperties'
        throw new IllegalArgumentException("'localNodeProperties' is mandatory when 'customProperties' is provided!");
      }

      // validate
      NodeProperties.from(localNodeProperties);

      clusterProperties = customProperties;
      nodeProperties = localNodeProperties;
    }

    return Grid.start(worldName, Configuration.define(), clusterProperties, nodeProperties);
  }

  public static int serverPort() {
    return Settings.serverPort();
  }
}