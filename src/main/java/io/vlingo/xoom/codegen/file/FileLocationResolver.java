// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.file;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.CodeGenerationLocation;
import io.vlingo.xoom.codegen.template.TemplateData;

import static io.vlingo.xoom.codegen.CodeGenerationParameter.GENERATION_LOCATION;

public interface FileLocationResolver {

    static String from(final CodeGenerationContext context,
                       final TemplateData templateData) {

        final CodeGenerationLocation location =
                context.parameterOf(GENERATION_LOCATION,
                        value -> CodeGenerationLocation.valueOf(value));

        if(location.isInternal()) {
            return new InternalFileLocationResolver().resolve(context, templateData);
        }

        return new ExternalFileLocationResolver().resolve(context, templateData);
    }

    String resolve(final CodeGenerationContext context, final TemplateData templateData);
}
