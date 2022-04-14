// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.autodispatch;

public class HandlerEntry<T extends Handler> {

  public final int index;
  public final T handler;

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static <T extends Handler> HandlerEntry<T> of(final int index,
                                                       final T handler) {
    return new HandlerEntry(index, handler);
  }

  private HandlerEntry(final int index,
                       final T handler) {
    this.index = index;
    this.handler = handler;
  }

}
