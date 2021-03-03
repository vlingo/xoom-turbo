// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.model.aggregate;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;

import java.util.Arrays;
import java.util.List;

import static io.vlingo.xoom.codegen.parameter.Label.*;

public class FieldDetail {

    private static String UNKNOWN_FIELD_MESSAGE = "%s is not a field in %s state";
    private static final List<String> NUMERIC_TYPES = Arrays.asList("byte", "short", "int", "integer", "long", "double", "float");

    @SuppressWarnings("static-access")
    public static String typeOf(final CodeGenerationParameter parent, final String fieldName) {
        return parent.retrieveAllRelated(resolveFieldTypeLabel(parent))
                .filter(stateField -> stateField.value.equals(fieldName))
                .map(stateField -> stateField.retrieveRelatedValue(FIELD_TYPE)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(UNKNOWN_FIELD_MESSAGE.format(fieldName, parent.value)));
    }

    public static String resolveDefaultValue(final CodeGenerationParameter parent, final String stateFieldName) {
        final String type = typeOf(parent, stateFieldName);
        if(type.equalsIgnoreCase(Boolean.class.getSimpleName())) {
            return "false";
        }
        if(NUMERIC_TYPES.contains(typeOf(parent, stateFieldName).toLowerCase())) {
            return "0";
        }
        return "null";
    }

    private static Label resolveFieldTypeLabel(final CodeGenerationParameter parent) {
        if(parent.isLabeled(AGGREGATE)) {
            return STATE_FIELD;
        }
        if(parent.isLabeled(VALUE_OBJECT)) {
            return VALUE_OBJECT_FIELD;
        }
        throw new IllegalArgumentException("Unable to resolve field type of " + parent.label);
    }
}
