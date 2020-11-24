// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.resource;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;

import static io.vlingo.xoom.codegen.parameter.Label.ROUTE_PATH;
import static io.vlingo.xoom.codegen.parameter.Label.URI_ROOT;

public class RoutePathFormatter {

    private static final String ROUTE_PATH_PATTERN = "%s/%s";

    public static String formatFullPath(final CodeGenerationParameter routeParameter) {
        final String routePath = routeParameter.retrieveRelatedValue(ROUTE_PATH);
        final String uriRoot = routeParameter.parent().retrieveRelatedValue(URI_ROOT);

        if(routePath.isEmpty() || routePath.equals("/") ){
            return uriRoot;
        }

        return removeSurplusesSlashes(String.format(ROUTE_PATH_PATTERN, uriRoot, routePath));
    }

    private static String removeSurplusesSlashes(final String path) {
        String cleanPath = path;
        while(cleanPath.contains("//")) {
            cleanPath = cleanPath.replaceAll("//", "/");
        }
        return cleanPath;
    }

}
