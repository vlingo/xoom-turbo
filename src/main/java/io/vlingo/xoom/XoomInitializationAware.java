// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom;

import io.vlingo.actors.Stage;
import io.vlingo.http.resource.Configuration;
import io.vlingo.http.resource.StaticFilesConfiguration;
import io.vlingo.http.resource.feed.FeedConfiguration;
import io.vlingo.http.resource.sse.SseConfiguration;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.dispatch.NoOpDispatcher;

public interface XoomInitializationAware {

    Integer DEFAULT_PORT = 18080;

    void onInit(final Stage stage);

    default Configuration configureServer(final Stage stage, final String[] args) {
        final int port = resolvePort(stage, args);
        return Configuration.define().withPort(port);
    }

    default Dispatcher exchangeDispatcher(final Stage stage) {
        return new NoOpDispatcher();
    }

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

    default int resolvePort(final Stage stage, final String[] args) {
        try {
            return Integer.parseInt(args[0]);
        } catch (final Exception e) {
            System.out.println(stage.world().name() + ": Command line does not provide a valid port; defaulting to: " + DEFAULT_PORT);
            return DEFAULT_PORT;
        }
    }
}
