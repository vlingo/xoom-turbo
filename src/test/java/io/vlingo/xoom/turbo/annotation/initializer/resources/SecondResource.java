// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.initializer.resources;

import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.http.resource.DynamicResourceHandler;
import io.vlingo.xoom.http.resource.ObjectResponse;
import io.vlingo.xoom.http.resource.Resource;
import io.vlingo.xoom.http.resource.ResourceHandler;

import static io.vlingo.xoom.http.Response.Status.Ok;
import static io.vlingo.xoom.http.resource.ResourceBuilder.get;
import static io.vlingo.xoom.http.resource.ResourceBuilder.resource;

public class SecondResource extends DynamicResourceHandler {

    public SecondResource(final Stage stage) {
        super(stage);
    }

    public Completes<ObjectResponse<String>> retrieveResource() {
        return Completes.withSuccess(ObjectResponse.of(Ok, "second-resource"));
    }

    @Override
    public Resource<?> routes() {
        return resource("SecondResource", get("/second-resource").handle(this::retrieveResource));
    }

}
