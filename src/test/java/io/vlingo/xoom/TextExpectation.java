// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom;

import io.vlingo.xoom.codegen.language.Language;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TextExpectation {

  private final Language language;

  public static TextExpectation onJava() {
    return new TextExpectation(Language.JAVA);
  }

  private TextExpectation(final Language language) {
    this.language = language;
  }

  public String read(final String textFileName) throws IOException {
    final String path =
            String.format("/text-expectations/%s/%s.text",
                    language.name().toLowerCase(), textFileName);

    final InputStream stream = TextExpectation.class.getResourceAsStream(path);

    return IOUtils.toString(stream, StandardCharsets.UTF_8.name());
  }
}
