// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.server;

import java.util.Optional;
import java.util.StringJoiner;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.env.Environment;
import io.micronaut.discovery.DefaultServiceInstanceIdGenerator;
import io.micronaut.discovery.ServiceInstance;
import io.micronaut.discovery.ServiceInstanceIdGenerator;
import io.micronaut.runtime.server.EmbeddedServerInstance;

@Singleton
@Replaces(DefaultServiceInstanceIdGenerator.class)
public class VlingoServiceInstanceIdGenerator implements ServiceInstanceIdGenerator {
    @Nonnull
    @Override
    public String generateId(Environment environment, ServiceInstance serviceInstance) {
        Optional<String> cloudFoundryId = environment.getProperty("vcap.application.instance_id", String.class);
        if (cloudFoundryId.isPresent()) {
            return cloudFoundryId.get();
        } else {
            StringJoiner joiner = new StringJoiner(":");
            if (serviceInstance instanceof EmbeddedServerInstance) {
                EmbeddedServerInstance esi = (EmbeddedServerInstance) serviceInstance;
                Optional<String> id = Optional.ofNullable(serviceInstance.getHost());

                if (id.isPresent()) {
                    if (id.get().equals("localhost")) {
                        joiner.add(serviceInstance.getId());
                        joiner.add(String.valueOf(esi.getPort()));
                    } else {
                        joiner.add(id.get());
                    }
                } else {
                    joiner.add(String.valueOf(esi.getPort()));
                }

            } else {
                joiner.add(String.valueOf(serviceInstance.getPort()));
            }

            return joiner.toString();
        }
    }
}
