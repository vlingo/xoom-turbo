// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.codegen.template;

import io.vlingo.xoom.turbo.codegen.CodeGenerationContext;
import io.vlingo.xoom.turbo.codegen.file.FileLocationResolver;
import io.vlingo.xoom.turbo.codegen.language.Language;

import java.io.File;
import java.nio.file.Paths;

import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.OFFSET;

public class OutputFile {

  private final String absolutePath;
  private final String filename;
  private final String offset;
  private final boolean placeholder;

  public OutputFile(final CodeGenerationContext context,
                    final TemplateData templateData,
                    final Language language) {
    this(FileLocationResolver.from(context, templateData),
            language.formatFilename(templateData.filename()), templateData.parameters().find(OFFSET),
            templateData.isPlaceholder());
  }

  public OutputFile(final String absolutePath,
                    final String filename) {
    this(absolutePath, filename, "", false);
  }

  private OutputFile(final String absolutePath,
                     final String filename,
                     final String offset,
                     final boolean placeholder) {
    this.absolutePath = absolutePath;
    this.filename = filename;
    this.offset = offset;
    this.placeholder = placeholder;
  }

  public boolean isPlaceholder() {
    return placeholder;
  }

  public String filename() {
    return filename;
  }

  public String filePath() {
    return Paths.get(absolutePath, filename).toString();
  }

  public File toFile() {
    return new File(filePath());
  }

  public String offset() {
    return offset;
  }

}
