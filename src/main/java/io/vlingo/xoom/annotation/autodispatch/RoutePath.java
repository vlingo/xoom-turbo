// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.autodispatch;

public class RoutePath {

    private static final String PATTERN = "/%s/%s";

    public static String resolve(final String root, final String subPath) {
        final String routePath = String.format(PATTERN, root, subPath).replaceAll("//", "/");
        if(routePath.endsWith("/")) {
            return routePath.substring(0, routePath.lastIndexOf("/"));
        }
        return routePath;
    }

}
