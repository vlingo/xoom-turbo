// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.autodispatch;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.content.CodeElementFormatter;
import io.vlingo.xoom.codegen.template.storage.Queries;

import java.util.Optional;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AUTO_DISPATCH_RESOURCE_HANDLER;

public class AutoDispatchResourceHandlerDetail {

  public static Optional<String> findQueriesAttributeName(final String qualifiedName, final CodeGenerationContext context) {
    final String packageName = CodeElementFormatter.packageOf(qualifiedName);
    final String className = CodeElementFormatter.simpleNameOf(qualifiedName);
    return context.templateParametersOf(AUTO_DISPATCH_RESOURCE_HANDLER).stream()
            .filter(data -> data.parameters().find(REST_RESOURCE_NAME).equals(className))
            .filter(data -> data.parameters().find(PACKAGE_NAME).equals(packageName))
            .map(data -> data.parameters().<Queries>find(QUERIES).getAttributeName())
            .filter(attributeName -> !attributeName.isEmpty()).findFirst();
  }

}
