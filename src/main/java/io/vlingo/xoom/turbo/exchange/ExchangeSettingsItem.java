// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.exchange;

public class ExchangeSettingsItem {

  public final String key;
  public final String value;

  public ExchangeSettingsItem(final String key, final String value) {
    this.key = key;
    this.value = value;
  }

  public boolean hasKey(final String key) {
    return this.key.equals(key);
  }
}
