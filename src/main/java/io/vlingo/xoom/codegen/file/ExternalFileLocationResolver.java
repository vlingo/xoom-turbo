// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.file;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.template.TemplateData;
import org.apache.commons.lang3.ArrayUtils;

import java.nio.file.Paths;

import static io.vlingo.xoom.codegen.CodeGenerationParameter.APPLICATION_NAME;
import static io.vlingo.xoom.codegen.CodeGenerationParameter.TARGET_FOLDER;
import static io.vlingo.xoom.codegen.template.TemplateParameter.PACKAGE_NAME;

public class ExternalFileLocationResolver implements FileLocationResolver {

    private static final String[] SOURCE_FOLDER = {"src", "main", "java"};

    @Override
    public String resolve(final CodeGenerationContext context,
                          final TemplateData templateData) {
        final String projectPath = resolveProjectPath(context);
        final String[] sourceFolders = listSourceFolders(templateData);
        return Paths.get(projectPath, sourceFolders).toString();
    }

    private String resolveProjectPath(final CodeGenerationContext context) {
        final String appName = context.parameterOf(APPLICATION_NAME);
        final String targetFolder = context.parameterOf(TARGET_FOLDER);
        return Paths.get(targetFolder, appName).toString();
    }

    private String[] listSourceFolders(final TemplateData templateData) {
        final String packageName = templateData.parameters().find(PACKAGE_NAME);
        return ArrayUtils.addAll(SOURCE_FOLDER, packageName.split("\\."));
    }
    
}
