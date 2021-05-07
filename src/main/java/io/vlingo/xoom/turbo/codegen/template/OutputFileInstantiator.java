// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.codegen.template;

import io.vlingo.xoom.turbo.codegen.CodeGenerationContext;
import io.vlingo.xoom.turbo.codegen.language.Language;

public interface OutputFileInstantiator {

  OutputFile instantiate(final CodeGenerationContext context,
                         final TemplateData templateData,
                         final Language language);

  static OutputFileInstantiator defaultInstantiation() {
    return (context, data, language) -> {
      final String absolutePath = context.fileLocationResolver().resolve(context, data);
      final String filename = language.formatFilename(data.filename());
      return new OutputFile(absolutePath, filename, "", data.isPlaceholder());
    };
  }
}
