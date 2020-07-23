// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.content;

import io.vlingo.xoom.codegen.CodeGenerationException;
import io.vlingo.xoom.codegen.template.TemplateFile;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TextBasedContent extends Content {

    public final File file;
    public final String text;

    public TextBasedContent(final TemplateStandard standard,
                            final TemplateFile templateFile,
                            final String text) {
        super(standard);
        this.text = text;
        this.file = new File(templateFile.filePath());
    }

    @Override
    public void create() {
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
            Files.write(file.toPath(), text.getBytes());
        } catch (final IOException e) {
            throw new CodeGenerationException(e);
        }
    }

    @Override
    public String retrieveClassName() {
        return FilenameUtils.removeExtension(file.getName());
    }

    @Override
    public String retrievePackage() {
        final int packageStartIndex = text.indexOf("package");
        final int packageEndIndex = text.indexOf(";");
        return text.substring(packageStartIndex + 8, packageEndIndex);
    }

    @Override
    public String retrieveFullyQualifiedName() {
        return String.format("%s.%s", retrievePackage(), retrieveClassName());
    }

    @Override
    public boolean contains(final String term) {
        return text.contains(term);
    }

    @Override
    public boolean canWrite() {
        return true;
    }

}
