// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.autodispatch;

import io.vlingo.xoom.turbo.annotation.TypeReader;

import javax.lang.model.element.VariableElement;

public class HandlerInvocation {

  private static final String DEFAULT_HANDLER_INVOCATION_PATTERN = "%s.handler.handle";
  private static final String PARAMETRIZED_HANDLER_INVOCATION_PATTERN = DEFAULT_HANDLER_INVOCATION_PATTERN + "(%s)";

  public final int index;
  public final String invocation;
  private final TypeReader handlersConfigReader;

  public HandlerInvocation(final TypeReader handlersConfigReader,
                           final VariableElement handlerEntry) {
    this.handlersConfigReader = handlersConfigReader;
    this.index = findIndex(handlerEntry);
    this.invocation = resolveInvocation(handlerEntry);
  }

  private int findIndex(final VariableElement handlerEntry) {
    final String handlerEntryValue =
            handlersConfigReader.findMemberValue(handlerEntry);

    final String handlerEntryIndex =
            handlerEntryValue.substring(handlerEntryValue.indexOf("(") + 1,
                    handlerEntryValue.indexOf(","));
    try {
      return Integer.parseInt(handlerEntryIndex);
    } catch (final NumberFormatException exception) {
      return Integer.parseInt(handlersConfigReader.findMemberValue(handlerEntryIndex));
    }
  }

  private String resolveInvocation(final VariableElement handlerEntry) {
    final String handlerEntryValue =
            handlersConfigReader.findMemberValue(handlerEntry);

    if (handlerEntryValue.contains("->")) {
      final String handlerInvocationArguments = extractHandlerArguments(handlerEntryValue);
      return String.format(PARAMETRIZED_HANDLER_INVOCATION_PATTERN,
              handlerEntry.getSimpleName(), handlerInvocationArguments);
    }

    return String.format(DEFAULT_HANDLER_INVOCATION_PATTERN, handlerEntry.getSimpleName());
  }

  private String extractHandlerArguments(final String handlerEntryValue) {
    return handlerEntryValue.substring(handlerEntryValue.indexOf(",") + 1, handlerEntryValue.indexOf("->"))
            .replaceAll("\\(", "").replaceAll("\\)", "").trim();
  }

  public boolean hasCustomParamNames() {
    return invocation.contains("(") && invocation.contains(")");
  }

}