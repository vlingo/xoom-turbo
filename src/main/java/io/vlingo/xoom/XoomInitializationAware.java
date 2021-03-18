// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom;

import io.vlingo.actors.Grid;
import io.vlingo.cluster.model.Properties;
import io.vlingo.http.resource.Configuration;
import io.vlingo.http.resource.StaticFilesConfiguration;
import io.vlingo.http.resource.feed.FeedConfiguration;
import io.vlingo.http.resource.sse.SseConfiguration;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.dispatch.NoOpDispatcher;

public interface XoomInitializationAware {

    void onInit(final Grid grid);

    /**
     * Answer an unconfigured {@code FeedConfiguration}.
     * @return FeedConfiguration
     */
    default FeedConfiguration feedConfiguration() {
      return FeedConfiguration.define();
    }

    /**
     * Answer an unconfigured {@code SseConfiguration}.
     * @return SseConfiguration
     */
    default SseConfiguration sseConfiguration() {
      return SseConfiguration.define();
    }

    /**
     * Answer an unconfigured {@code StaticFilesConfiguration}.
     * @return StaticFilesConfiguration
     */
    default StaticFilesConfiguration staticFilesConfiguration() {
      return StaticFilesConfiguration.define();
    }

    @SuppressWarnings("rawtypes")
    default Dispatcher exchangeDispatcher(final Grid grid) {
        return new NoOpDispatcher();
    }

    default Configuration configureServer(final Grid grid, final String[] args) {
        final int port = resolveServerPort(grid);
        return Configuration.define().withPort(port);
    }

    default int resolveServerPort(final Grid grid) {
        final int port = Boot.serverPort();
        grid.world().defaultLogger().info("Server running on " + port);
        return port;
    }

    default String parseNodeName(final String[] args) {
        if (args.length == 0) {
            return null;
        } else if (args.length > 1) {
            System.out.println("Too many arguments; provide node name only.");
            System.exit(1);
        }
        return args[0];
    }

    default Properties clusterProperties() {
        return null;
    }


}
