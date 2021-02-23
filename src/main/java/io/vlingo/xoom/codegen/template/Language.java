// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template;

public enum Language {
  JAVA("java"),
  KOTLIN("kt"),
  C_SHARP("cs"),
  F_SHARP("fs");

  private final String extension;

  Language(final String extension) {
    this.extension = extension;
  }

  public static Language findDefault() {
    return JAVA;
  }

  public String formatFilename(final String fileName) {
    if(fileName.contains(".")) {
      return fileName;
    }
    return fileName + "." + extension;
  }

  public String templateFolderName() {
    return name().toLowerCase();
  }
}