// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.file.FileLocationResolver;

import java.io.File;
import java.nio.file.Paths;

public class TemplateFile {

    private final String absolutePath;
    private final String filename;
    private final boolean placeholder;

    public TemplateFile(final CodeGenerationContext context,
                        final TemplateData templateData) {
        this(context.isInternalGeneration() ? "" : FileLocationResolver.from(context, templateData),
                templateData.filename(), templateData.isPlaceholder());
    }

    public TemplateFile(final String absolutePath,
                        final String filename) {
        this(absolutePath, filename, false);
    }

    private TemplateFile(final String absolutePath,
                        final String filename,
                        final boolean placeholder) {
        this.absolutePath = absolutePath;
        this.filename = filename;
        this.placeholder = placeholder;
    }

    public String filename() {
        return filename;
    }

    public String filePath() {
        return Paths.get(absolutePath, filename).toString();
    }

    public File toFile() {
        return new File(filePath());
    }

}
