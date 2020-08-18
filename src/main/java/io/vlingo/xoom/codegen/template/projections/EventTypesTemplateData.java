// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.projections;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import java.util.List;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.DOMAIN_EVENT;
import static io.vlingo.xoom.codegen.template.TemplateStandard.EVENT_TYPES;

public class EventTypesTemplateData extends TemplateData {

    private final static String PACKAGE_PATTERN = "%s.%s";
    private final static String INFRASTRUCTURE_PACKAGE = "infrastructure";

    private final TemplateParameters parameters;

    public static EventTypesTemplateData from(final String basePackage,
                                              final List<Content> contents) {
        return new EventTypesTemplateData(basePackage, contents);
    }

    private EventTypesTemplateData(final String basePackage,
                                  final List<Content> contents) {
        final String packageName = resolvePackage(basePackage);
        final String className = EVENT_TYPES.resolveClassname();
        final String qualifiedName = packageName + "." + className;

        this.parameters =
                TemplateParameters.with(PACKAGE_NAME, packageName)
                        .and(EVENTS_NAMES, ContentQuery.findClassNames(DOMAIN_EVENT, contents))
                        .and(EVENT_TYPES_NAME, className).and(EVENT_TYPES_QUALIFIED_NAME, qualifiedName);
    }

    private String resolvePackage(final String basePackage) {
        return String.format(PACKAGE_PATTERN, basePackage, INFRASTRUCTURE_PACKAGE).toLowerCase();
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public TemplateStandard standard() {
        return EVENT_TYPES;
    }

    @Override
    public String filename() {
        return EVENT_TYPES.resolveFilename(parameters);
    }

}
