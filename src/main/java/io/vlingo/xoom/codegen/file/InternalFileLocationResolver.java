// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.file;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.template.TemplateData;

import java.nio.file.Paths;

import static io.vlingo.xoom.codegen.template.TemplateParameter.PACKAGE_NAME;

public class InternalFileLocationResolver implements FileLocationResolver {

    @Override
    public String resolve(final CodeGenerationContext context,
                          final TemplateData templateData) {
        final String packageName =
                templateData.parameters().find(PACKAGE_NAME);

        final String internalRequesterPath =
                context.internalTargetFolder();

        return Paths.get(internalRequesterPath, packageName.split("\\.")).toString();
    }

}