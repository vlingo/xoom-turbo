// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.autodispatch;

import com.sun.source.util.Trees;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import java.util.List;
import java.util.function.Predicate;

public class HandlerInvocation {

    private static final String DEFAULT_HANDLER_INVOCATION_PATTERN = "%s.handler.handle";
    private static final String PARAMETRIZED_HANDLER_INVOCATION_PATTERN = DEFAULT_HANDLER_INVOCATION_PATTERN + "(%s)";

    public final int index;
    public final String invocation;
    private final Trees sourceCode;
    private final List<? extends Element> configMembers;

    public HandlerInvocation(final Trees sourceCode,
                             final Element handlerEntry,
                             final List<? extends Element> configMembers) {
        this.sourceCode = sourceCode;
        this.configMembers = configMembers;
        this.index = findIndex(handlerEntry);
        this.invocation = resolveInvocation(handlerEntry);
    }

    private int findIndex(final Element handlerEntry) {
        final String handlerEntryValue = retrieveHandlerEntryValue(handlerEntry);

        final String handlerEntryIndex =
                handlerEntryValue.substring(handlerEntryValue.indexOf("(") + 1,
                        handlerEntryValue.indexOf(","));
        try {
            return Integer.parseInt(handlerEntryIndex);
        } catch (final NumberFormatException exception) {
            return findMemberValue(handlerEntryIndex);
        }
    }

    private int findMemberValue(final String memberName) {
        final Predicate<Element> memberFilter =
                element -> element.getSimpleName().toString().equals(memberName);

        return configMembers.stream().filter(memberFilter)
                .map(member -> ((VariableElement) member).getConstantValue())
                .mapToInt(value -> Integer.parseInt(value.toString())).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Member " + memberName + " not found"));
    }

    private String resolveInvocation(final Element handlerEntry) {
        final String handlerEntryValue = retrieveHandlerEntryValue(handlerEntry);

        if(handlerEntryValue.contains("->")) {
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

    private String retrieveHandlerEntryValue(final Element handlerEntry) {
        return sourceCode.getTree(handlerEntry).toString().split("=")[1].trim();
    }

    public boolean hasCustomParamNames() {
        return invocation.contains("(") && invocation.contains(")");
    }
}
