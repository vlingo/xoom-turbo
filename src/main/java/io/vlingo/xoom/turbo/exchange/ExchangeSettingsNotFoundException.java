// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.exchange;

import java.util.List;
import java.util.stream.Collectors;

public class ExchangeSettingsNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ExchangeSettingsNotFoundException(final List<String> parameters) {
    super("The following exchange parameter(s) were not informed: " + parameters.stream().collect(Collectors.joining(", ")));
  }

}
