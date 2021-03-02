// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.model.formatting;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.model.MethodScope;

public interface AggregateArgumentsFormat {

    AggregateArgumentsFormat METHOD_INVOCATION = new MethodInvocation("stage");
    AggregateArgumentsFormat SIGNATURE_DECLARATION = new SignatureDeclaration();

    default String format(final CodeGenerationParameter parameter) {
        return format(parameter, MethodScope.INSTANCE);
    }

    String format(final CodeGenerationParameter parameter, final MethodScope scope);

}
