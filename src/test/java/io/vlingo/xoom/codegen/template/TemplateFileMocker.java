// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template;

import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class TemplateFileMocker {

    public static TemplateFile mock(final String absolutePath,
                                    final String fileName) {
        final TemplateFile templateFile = Mockito.mock(TemplateFile.class);
        when(templateFile.absolutePath()).thenReturn(absolutePath);
        when(templateFile.filename()).thenReturn(fileName);
        return templateFile;
    }
}
