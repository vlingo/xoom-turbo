// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.codegen.initializer;

import io.vlingo.xoom.codegen.content.CodeElementFormatter;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.turbo.ComponentRegistry;
import io.vlingo.xoom.turbo.annotation.codegen.AnnotationBasedTemplateStandard;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class RestResource {

  public final String className;
  public final String objectName;

  public static List<RestResource> from(final List<Content> contents) {
    final CodeElementFormatter formatter =
            ComponentRegistry.withType(CodeElementFormatter.class);

    final Set<String> classNames =
            ContentQuery.findClassNames(contents, AnnotationBasedTemplateStandard.REST_RESOURCE, AnnotationBasedTemplateStandard.AUTO_DISPATCH_RESOURCE_HANDLER);

    return classNames.stream().map(className -> new RestResource(formatter, className)).collect(toList());
  }

  private RestResource(final CodeElementFormatter formatter,
                       final String restResourceName) {
    this.className = restResourceName;
    this.objectName = formatter.simpleNameToAttribute(restResourceName);
  }


}
