// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template;

import io.vlingo.xoom.codegen.CodeGenerationContext;

public class TemplateFile {

    private final String absolutePath;
    private final String filename;

    public TemplateFile(final CodeGenerationContext context,
                        final TemplateData templateData) {
        this.absolutePath = FileLocationResolver.from(context, templateData);
        this.filename = templateData.filename();
    }

    public String absolutePath() {
        return absolutePath;
    }

    public String filename() {
        return filename;
    }

}
