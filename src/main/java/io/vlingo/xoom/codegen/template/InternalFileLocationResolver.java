// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.nio.file.Paths;

import static io.vlingo.xoom.codegen.CodeGenerationSetup.TEST_CLASSES_FOLDER_NAME;
import static io.vlingo.xoom.codegen.template.TemplateParameter.PACKAGE_NAME;

public class InternalFileLocationResolver implements FileLocationResolver {

    private static final String TEST_TARGET_FOLDER = "generated-test-sources";
    private static final String PRODUCTION_TARGET_FOLDER = "generated-sources";

    @Override
    public String resolve(final CodeGenerationContext context,
                          final TemplateData templateData) {
        final String internalRequesterPath =
                context.internalRequesterPath();

        final String parentFolder =
                Paths.get(internalRequesterPath).getParent().toString();

        final String targetFolder =
                isOnTestEnvironment(internalRequesterPath) ?
                        TEST_TARGET_FOLDER : PRODUCTION_TARGET_FOLDER;

        final String[] sourceFolders =
                listSourceFolders(targetFolder, templateData);

        return Paths.get(parentFolder, sourceFolders).toString();
    }

    private boolean isOnTestEnvironment(final String internalRequesterPath) {
        if(internalRequesterPath.endsWith(TEST_CLASSES_FOLDER_NAME + File.separator)) {
            return true;
        }
        return false;
    }

    private String[] listSourceFolders(final String targetFolder, final TemplateData templateData) {
        final String packageName = templateData.parameters().find(PACKAGE_NAME);
        return ArrayUtils.addAll(new String[]{targetFolder}, packageName.split("\\."));
    }

}