// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.codegen.language;

import org.junit.Assert;
import org.junit.Test;

public class KotlinSyntaxConverterTest {

  @Test
  public void testThatImportSyntaxIsResolved() {
    Assert.assertEquals("io.vlingo.xoom.symbio.store.`object`.StateObject", KotlinSyntaxConverter.handleImportEntry("io.vlingo.xoom.symbio.store.object.StateObject"));
    Assert.assertEquals("`object`.fakePackage.`object`.Something", KotlinSyntaxConverter.handleImportEntry("object.fakePackage.object.Something"));
    Assert.assertEquals("no.reservedwords.here.MyClass", KotlinSyntaxConverter.handleImportEntry("no.reservedwords.here.MyClass"));
  }

}
