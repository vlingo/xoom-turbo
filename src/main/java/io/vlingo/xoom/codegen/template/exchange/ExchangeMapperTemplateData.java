// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.exchange;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameter;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import static io.vlingo.xoom.codegen.parameter.Label.LOCAL_TYPE;

public class ExchangeMapperTemplateData extends TemplateData {


    private final TemplateParameters parameters;

    public ExchangeMapperTemplateData(final String exchangePackage,
                                      final String aggregateProtocolName,
                                      final CodeGenerationParameter convey) {
        this.parameters =
                TemplateParameters.with(TemplateParameter.PACKAGE_NAME, exchangePackage)
                    .and(TemplateParameter.AGGREGATE_PROTOCOL_NAME, aggregateProtocolName)
                    .and(TemplateParameter.LOCAL_TYPE_NAME, convey.retrieveRelatedValue(LOCAL_TYPE));
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public TemplateStandard standard() {
        return TemplateStandard.EXCHANGE_MAPPER;
    }
}
