// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.codegen.content;

import io.vlingo.xoom.turbo.codegen.CodeGenerationContext;
import io.vlingo.xoom.turbo.codegen.CodeGenerationStep;

public class ContentCreationStep implements CodeGenerationStep {

    @Override
    public void process(final CodeGenerationContext context) {
        context.contents().stream().filter(Content::canWrite).forEach(Content::create);
    }

}
