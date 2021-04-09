// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.codegen.template.model;

import io.vlingo.xoom.turbo.codegen.CodeGenerationSetup;
import io.vlingo.xoom.turbo.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.turbo.codegen.parameter.Label;

import static io.vlingo.xoom.turbo.codegen.CodeGenerationSetup.SCALAR_NUMERIC_TYPES;
import static io.vlingo.xoom.turbo.codegen.parameter.Label.*;

public class FieldDetail {

    private static String UNKNOWN_FIELD_MESSAGE = "%s is not a field in %s state";

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
        if(SCALAR_NUMERIC_TYPES.contains(typeOf(parent, stateFieldName).toLowerCase())) {
            return "0";
        }
        return "null";
    }

    public static boolean isScalar(final CodeGenerationParameter field) {
        final String fieldType =
                field.retrieveRelatedValue(Label.FIELD_TYPE, String::toLowerCase);

        if(fieldType == null || fieldType.isEmpty()) {
            throw new IllegalArgumentException("Unable to find field type");
        }

        return CodeGenerationSetup.SCALAR_TYPES.contains(fieldType);
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
