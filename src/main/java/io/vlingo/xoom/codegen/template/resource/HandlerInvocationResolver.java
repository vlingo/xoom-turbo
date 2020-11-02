// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.resource;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.autodispatch.AutoDispatchHandlerInvocationResolver;

public interface HandlerInvocationResolver {

    String QUERIES_PARAMETER = "$queries";

    static HandlerInvocationResolver with(final CodeGenerationParameter parentParameter) {
        if(parentParameter.isLabeled(Label.AUTO_DISPATCH_NAME)) {
            return new AutoDispatchHandlerInvocationResolver();
        }
        return new DefaultHandlerInvocationResolver();
    }

    String resolveRouteHandlerInvocation(final CodeGenerationParameter parentParameter,
                                         final CodeGenerationParameter routeSignatureParameter);

    String resolveAdapterHandlerInvocation(final CodeGenerationParameter parentParameter,
                                           final CodeGenerationParameter routeSignatureParameter);

}
