// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;

import java.io.IOException;
import java.io.InputStream;

import static javax.tools.StandardLocation.SOURCE_PATH;

public class ClassFile {

    private final FileObject fileObject;

    public static final ClassFile from(final Filer filer,
                                       final TypeElement typeElement) throws IOException {
        return new ClassFile(filer, typeElement);
    }

    private ClassFile(final Filer filer,
                      final TypeElement typeElement) throws IOException {
        final String className =
                typeElement.getSimpleName() + ".java";

        final String packageName =
                typeElement.getEnclosingElement().toString();

        this.fileObject =
                filer.getResource(SOURCE_PATH, packageName, className);
    }

    public InputStream openInputStream() throws IOException {
        return fileObject.openInputStream();
    }
}
