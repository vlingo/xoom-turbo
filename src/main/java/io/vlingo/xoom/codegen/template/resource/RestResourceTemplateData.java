// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.resource;

import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.TemplateData;

import java.io.File;

import static io.vlingo.xoom.codegen.template.TemplateParameter.PACKAGE_NAME;
import static io.vlingo.xoom.codegen.template.TemplateParameter.REST_RESOURCE_NAME;
import static io.vlingo.xoom.codegen.template.TemplateStandard.REST_RESOURCE;

public class RestResourceTemplateData extends TemplateData {

    private final static String PACKAGE_PATTERN = "%s.%s";
    private final static String PARENT_PACKAGE_NAME = "resource";

    private final String packageName;
    private final String aggregateName;
    private final TemplateParameters parameters;

    public RestResourceTemplateData(final String aggregateName,
                                    final String basePackage) {
        this.aggregateName = aggregateName;
        this.packageName = resolvePackage(basePackage);
        this.parameters = loadParameters();
    }

    private TemplateParameters loadParameters() {
        return TemplateParameters
                .with(REST_RESOURCE_NAME, REST_RESOURCE.resolveClassname(aggregateName))
                .and(PACKAGE_NAME, packageName);
    }

    private String resolvePackage(final String basePackage) {
        return String.format(PACKAGE_PATTERN, basePackage, PARENT_PACKAGE_NAME).toLowerCase();
    }

    @Override
    public TemplateStandard standard() {
        return REST_RESOURCE;
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public String filename() {
        return standard().resolveFilename(aggregateName, parameters);
    }

}