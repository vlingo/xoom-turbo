// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.file;

import io.vlingo.xoom.OperatingSystem;
import io.vlingo.xoom.annotation.ProcessingAnnotationException;

import javax.annotation.processing.ProcessingEnvironment;
import java.io.IOException;
import java.nio.file.Paths;

public class InternalTargetPathLocator {

    public static String find(final ProcessingEnvironment environment) {
        try {
            final String rawPath =
                    environment.getFiler().createSourceFile("Temp").toUri().getPath();

            final String normalizedPath =
                    OperatingSystem.detect().isWindows() ? rawPath.substring(1) : rawPath;

            return Paths.get(normalizedPath).getParent().toString();
        } catch (final IOException exception) {
            exception.printStackTrace();
            throw new ProcessingAnnotationException(exception);
        }
    }

}
