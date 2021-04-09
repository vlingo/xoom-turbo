// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.codegen.file;

import io.vlingo.xoom.turbo.codegen.CodeGenerationContext;
import io.vlingo.xoom.turbo.codegen.CodeGenerationLocation;
import io.vlingo.xoom.turbo.codegen.template.TemplateData;

import static io.vlingo.xoom.turbo.codegen.parameter.Label.GENERATION_LOCATION;

public interface FileLocationResolver {

    static String from(final CodeGenerationContext context,
                       final TemplateData templateData) {

        final CodeGenerationLocation location =
                context.parameterOf(GENERATION_LOCATION,
                        value -> CodeGenerationLocation.valueOf(value));

        if(location.isInternal()) {
            throw new UnsupportedOperationException("Unable to resolve internal file location");
        }

        return new ExternalFileLocationResolver().resolve(context, templateData);
    }

    String resolve(final CodeGenerationContext context, final TemplateData templateData);
}
