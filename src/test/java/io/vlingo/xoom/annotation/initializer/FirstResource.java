// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.initializer;

import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;
import io.vlingo.http.resource.ObjectResponse;
import io.vlingo.http.resource.Resource;
import io.vlingo.http.resource.ResourceHandler;

import static io.vlingo.http.Response.Status.Ok;
import static io.vlingo.http.resource.ResourceBuilder.get;
import static io.vlingo.http.resource.ResourceBuilder.resource;

public class FirstResource extends ResourceHandler {


    public FirstResource(final Stage stage) {
    }

    public Completes<ObjectResponse<String>> retrieveResource() {
        return Completes.withSuccess(ObjectResponse.of(Ok, "first-resource"));
    }

    @Override
    public Resource<?> routes() {
        return resource("FirstResource", get("/first-resource").handle(this::retrieveResource));
    }

}
