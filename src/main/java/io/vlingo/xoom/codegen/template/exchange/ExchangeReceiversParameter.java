// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.exchange;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import java.util.List;
import java.util.stream.Collectors;

public class ExchangeReceiversParameter {

    private final String schemaName;
    private final String localTypeName;

    public static List<ExchangeReceiversParameter> from(final CodeGenerationParameter exchange) {
        return exchange.retrieveAllRelated(Label.SCHEMA)
                .map(schema -> new ExchangeReceiversParameter(exchange, schema))
                .collect(Collectors.toList());
    }

    private ExchangeReceiversParameter(final CodeGenerationParameter exchange,
                                       final CodeGenerationParameter schema) {
        this.schemaName = schema.value;
        this.localTypeName = TemplateStandard.DATA_OBJECT.resolveClassname(exchange.parent().value);
    }

}
