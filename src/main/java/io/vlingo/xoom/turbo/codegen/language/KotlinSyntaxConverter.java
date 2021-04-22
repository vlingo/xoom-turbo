// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.codegen.language;

import freemarker.template.utility.StringUtil;
import io.vlingo.xoom.turbo.codegen.CodeGenerationSetup;
import io.vlingo.xoom.turbo.codegen.parameter.CodeGenerationParameters;
import io.vlingo.xoom.turbo.codegen.template.TemplateParameters;

import static io.vlingo.xoom.turbo.codegen.language.Language.KOTLIN;
import static io.vlingo.xoom.turbo.codegen.parameter.Label.FIELD_TYPE;
import static io.vlingo.xoom.turbo.codegen.parameter.Label.STATE_FIELD;

public class KotlinSyntaxConverter {

  public static final void convertFieldTypes(final CodeGenerationParameters params) {
    params.convertValuesSyntax(KOTLIN, STATE_FIELD, FIELD_TYPE, value -> StringUtil.capitalize(value));
  }

  public static final void convertImports(final TemplateParameters params) {
    params.convertImportSyntax(KotlinSyntaxConverter::handleImportEntry);
  }

  protected static final String handleImportEntry(final String importEntry) {
    String resolvedImport = importEntry;
    for (final String reservedWord : CodeGenerationSetup.KOTLIN_RESERVED_WORDS) {
      resolvedImport = handleReservedWord(reservedWord, resolvedImport);
    }
    return resolvedImport;
  }

  private static final String handleReservedWord(final String reservedWord, final String importEntry) {
    final String resolvedImport =
            !importEntry.startsWith(reservedWord + ".") ? importEntry :
                    importEntry.replaceFirst(reservedWord + "\\.", "`" + reservedWord + "`.");
    return resolvedImport.replaceAll("\\." + reservedWord + "\\.", ".`" + reservedWord + "`.");
  }
}
