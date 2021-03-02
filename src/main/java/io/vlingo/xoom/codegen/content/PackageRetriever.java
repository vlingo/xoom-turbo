// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.content;

import java.util.stream.Stream;

public interface PackageRetriever {

  String find(final String text);

  boolean support(final String text);

  static String retrieve(final String text) {
    return Stream.of(new DefaultPackageRetriever(),
            new AgnosticPackageRetriever()).filter(retriever -> retriever.support(text))
            .map(retriever -> retriever.find(text)).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unable to find package"));
  }

  class DefaultPackageRetriever implements PackageRetriever {

    @Override
    public String find(final String text) {
      final int packageStartIndex = text.indexOf("package");
      final int packageEndIndex = text.indexOf("\\n", packageStartIndex + 8);
      return text.substring(packageStartIndex + 8, packageEndIndex)
              .replaceAll(";", "").trim();
    }

    @Override
    public boolean support(final String text) {
      return text.indexOf("\\n") != -1;
    }
  }

  class AgnosticPackageRetriever implements PackageRetriever {

    @Override
    public String find(final String text) {
      return Stream.of(text.split("\\r?\\n")).filter(line -> line.startsWith("package"))
              .map(line -> line.replaceAll("package", "").replaceAll(";", "").trim())
              .findFirst().orElse("");
    }

    @Override
    public boolean support(final String text) {
      return text.split("\\r?\\n").length > 0;
    }
  }
}
