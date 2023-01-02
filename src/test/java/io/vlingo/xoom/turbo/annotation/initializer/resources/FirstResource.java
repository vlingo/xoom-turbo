// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.initializer.resources;

import static io.vlingo.xoom.http.Response.Status.Ok;
import static io.vlingo.xoom.http.resource.ResourceBuilder.get;
import static io.vlingo.xoom.http.resource.ResourceBuilder.resource;

import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.http.resource.DynamicResourceHandler;
import io.vlingo.xoom.http.resource.Resource;

public class FirstResource extends DynamicResourceHandler {

    public FirstResource(final Stage stage) {
        super(stage);
    }

    public Completes<Response> retrieveResource() {
        return Completes.withSuccess(Response.of(Ok, "first-resource"));
    }

    @Override
    public Resource<?> routes() {
        return resource("FirstResource",
                get("/first-resource").handle(this::retrieveResource));
    }
}
