// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static javax.tools.StandardLocation.CLASS_OUTPUT;

public class Context {

    private static final String TEST_OUTPUT_DIRECTORY = "test-classes";
    private static final String REFERENCE_DIRECTORY = "reference";

    public static Path locateBaseDirectory(final Filer filer) {
        Path ancestral = locateOutputFolder(filer).getParent();
        while(!ancestral.toFile().getName().equalsIgnoreCase("target")) {
            ancestral = ancestral.getParent();
        }
        return ancestral.getParent();
    }

    public static Path locateSourceFolder(final Filer filer) {
        final Path baseDirectory = locateBaseDirectory(filer);
        final String parentFolder = Context.isRunningTests(filer) ? "test" : "main";
        return baseDirectory.resolve("src").resolve(parentFolder).resolve("java");
    }

    private static Path locateOutputFolder(final Filer filer) {
        try {
            final FileObject referenceDirectory = filer.getResource(CLASS_OUTPUT, "", REFERENCE_DIRECTORY);
            return Paths.get(referenceDirectory.toUri()).getParent();
        } catch (final IOException e) {
            e.printStackTrace();
            throw new ProcessingAnnotationException("Unable to locate the output folder");
        }
    }

    public static boolean isRunningTests(final Filer filer) {
        final File outputFolder = locateOutputFolder(filer).toFile();
        return outputFolder.getName().equalsIgnoreCase(TEST_OUTPUT_DIRECTORY);
    }
}
