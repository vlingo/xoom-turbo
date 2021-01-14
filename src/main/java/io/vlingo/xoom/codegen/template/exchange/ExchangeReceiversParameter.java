// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.exchange;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;

import java.util.List;
import java.util.stream.Collectors;

import static io.vlingo.xoom.codegen.template.TemplateStandard.DATA_OBJECT;

public class ExchangeReceiversParameter {

    private final String schemaTypeName;
    private final String localTypeName;

    public static List<ExchangeReceiversParameter> from(final CodeGenerationParameter exchange) {
        return exchange.retrieveAllRelated(Label.SCHEMA)
                .map(schema -> new ExchangeReceiversParameter(exchange, schema))
                .collect(Collectors.toList());
    }

    private ExchangeReceiversParameter(final CodeGenerationParameter exchange,
                                       final CodeGenerationParameter schema) {
        this.schemaTypeName = resolveSchemaTypeName(schema);
        this.localTypeName = DATA_OBJECT.resolveClassname(exchange.parent().value);
    }

    private String resolveSchemaTypeName(final CodeGenerationParameter schema) {
        return schema.value.split(":")[3];
    }

    public String getSchemaTypeName() {
        return schemaTypeName;
    }

    public String getLocalTypeName() {
        return localTypeName;
    }

}
