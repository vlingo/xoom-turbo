// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.autodispatch;

import java.util.HashMap;
import java.util.Map;

public class Handlers {

    private Class<?> resourceClass;
    private final Map<Integer, Handler> handlers = new HashMap<>();

    public static Handlers of(final Class<?> resourceClass) {
        return new Handlers(resourceClass);
    }

    private Handlers(final Class<?> resourceClass) {
        if(resourceClass == null) {
            throw new IllegalArgumentException("Unable to associate a null resource class");
        }
        this.resourceClass = resourceClass;
    }

    public Handlers entry(final int handlerIndex, final Handler handler) {
        handlers.put(handlerIndex, handler);
        return this;
    }

    public Handler handlerOf(final int index) {
        if(!handlers.containsKey(index)) {
            new IllegalArgumentException("Handler not found. Index does not exist");
        }
        return handlers.get(index);
    }

}
