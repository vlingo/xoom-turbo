// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class ClassFile {

    private final File file;

    public static final ClassFile from(final Filer filer,
                                       final TypeElement typeElement) {
        return new ClassFile(filer, typeElement);
    }

    private ClassFile(final Filer filer,
                      final TypeElement typeElement) {
        final String className =
                typeElement.getSimpleName() + ".java";

        final String packageName =
                typeElement.getEnclosingElement().toString();

        this.file = findFile(filer, packageName, className);
    }

    private File findFile(final Filer filer,
                          final String packageName,
                          final String className) {
        final Path sourceFolder = Context.locateSourceFolder(filer);
        final String packagePath = packageName.replaceAll("\\.", "/");
        return sourceFolder.resolve(packagePath).resolve(className).toFile();
    }

    public InputStream openInputStream() throws IOException {
        return new FileInputStream(file);
    }
}
