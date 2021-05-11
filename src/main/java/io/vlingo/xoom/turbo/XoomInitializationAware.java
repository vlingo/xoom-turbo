// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo;

import io.vlingo.xoom.cluster.model.Properties;
import io.vlingo.xoom.http.resource.Configuration;
import io.vlingo.xoom.http.resource.StaticFilesConfiguration;
import io.vlingo.xoom.http.resource.feed.FeedConfiguration;
import io.vlingo.xoom.http.resource.sse.SseConfiguration;
import io.vlingo.xoom.lattice.grid.Grid;
import org.apache.commons.lang3.StringUtils;

import static io.vlingo.xoom.turbo.ApplicationProperty.NODE_NAME_ARG;
import static io.vlingo.xoom.turbo.ApplicationProperty.PORT_ARG;

public interface XoomInitializationAware {

  void onInit(final Grid grid);

  /**
   * Answer an unconfigured {@code FeedConfiguration}.
   *
   * @return FeedConfiguration
   */
  default FeedConfiguration feedConfiguration() {
    return FeedConfiguration.define();
  }

  /**
   * Answer an unconfigured {@code SseConfiguration}.
   *
   * @return SseConfiguration
   */
  default SseConfiguration sseConfiguration() {
    return SseConfiguration.define();
  }

  /**
   * Answer an unconfigured {@code StaticFilesConfiguration}.
   *
   * @return StaticFilesConfiguration
   */
  default StaticFilesConfiguration staticFilesConfiguration() {
    return StaticFilesConfiguration.define();
  }

  default Configuration configureServer(final Grid grid, final String[] args) {
    final Configuration configuration = Configuration.define();
    final String portArgument = ApplicationProperty.readValue(PORT_ARG, args);
    if(portArgument != null) {
      if(!StringUtils.isNumeric(portArgument)) {
        throw new IllegalArgumentException("The given server port is not valid");
      }
      configuration.withPort(Integer.valueOf(portArgument));
    } else {
      configuration.withPort(Boot.serverPort());
    }
    grid.world().defaultLogger().info("Server running on " + configuration.port());
    return configuration;
  }

  default String parseNodeName(final String[] args) {
    return ApplicationProperty.readValue(NODE_NAME_ARG, args);
  }

  default Properties clusterProperties() {
    return null;
  }
}
