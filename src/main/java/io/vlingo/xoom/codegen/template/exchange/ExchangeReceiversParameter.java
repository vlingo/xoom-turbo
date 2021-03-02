// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.exchange;

import io.vlingo.xoom.codegen.content.ClassFormatter;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.model.AggregateDetail;
import io.vlingo.xoom.codegen.template.model.MethodScope;
import io.vlingo.xoom.codegen.template.model.formatting.MethodInvocation;

import java.util.List;
import java.util.stream.Collectors;

import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.DATA_OBJECT;

public class ExchangeReceiversParameter {

    private final String schemaTypeName;
    private final String localTypeName;
    private final String modelProtocol;
    private final String modelMethod;
    private final String modelMethodParameters;
    private final boolean modelFactoryMethod;

    public static List<ExchangeReceiversParameter> from(final CodeGenerationParameter exchange) {
        return exchange.retrieveAllRelated(Label.RECEIVER)
                .map(receiver -> new ExchangeReceiversParameter(exchange, receiver))
                .collect(Collectors.toList());
    }

    private ExchangeReceiversParameter(final CodeGenerationParameter exchange,
                                       final CodeGenerationParameter receiver) {
        final CodeGenerationParameter aggregateMethod =
                AggregateDetail.methodWithName(exchange.parent(), receiver.retrieveRelatedValue(MODEL_METHOD));

        this.modelMethod = aggregateMethod.value;
        this.modelProtocol = exchange.parent(AGGREGATE).value;
        this.localTypeName = DATA_OBJECT.resolveClassname(exchange.parent().value);
        this.schemaTypeName = Formatter.formatSchemaTypeName(receiver.retrieveOneRelated(SCHEMA));
        this.modelFactoryMethod = aggregateMethod.retrieveRelatedValue(FACTORY_METHOD, Boolean::valueOf);
        this.modelMethodParameters = resolveModelMethodParameters(aggregateMethod);
    }

    private String resolveModelMethodParameters(final CodeGenerationParameter method) {
        final boolean factoryMethod = method.retrieveRelatedValue(Label.FACTORY_METHOD, Boolean::valueOf);
        final MethodScope methodScope = factoryMethod ? MethodScope.STATIC : MethodScope.INSTANCE;
        return new MethodInvocation("stage", "data").format(method, methodScope);
    }

    public String getSchemaTypeName() {
        return schemaTypeName;
    }

    public String getLocalTypeName() {
        return localTypeName;
    }

    public String getModelProtocol() {
        return modelProtocol;
    }

    public String getModelActor() {
        return TemplateStandard.AGGREGATE.resolveClassname(modelProtocol);
    }

    public String getModelMethod() {
        return modelMethod;
    }

    public String getModelMethodParameters() {
        return modelMethodParameters;
    }

    public String getModelVariable() {
        return ClassFormatter.simpleNameToAttribute(modelProtocol);
    }

    public boolean isModelFactoryMethod() {
        return modelFactoryMethod;
    }

}
