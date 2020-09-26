// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.storage;

import io.vlingo.xoom.codegen.content.ClassFormatter;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.QUERIES_ACTOR;

public class QueriesParameter {

    private static final String QUALIFIED_NAME_PATTERN = "%s.%s";

    private final String protocolName;
    private final String actorName;
    private final String attributeName;
    private final List<String> qualifiedNames = new ArrayList<>();

    public static List<QueriesParameter> from(final Boolean useCQRS,
                                              final List<Content> contents,
                                              final List<TemplateData> templatesData) {
        if(!useCQRS) {
            return Collections.emptyList();
        }

        return from(Model.QUERY, contents, templatesData);
    }

    public static List<QueriesParameter> from(final Model model,
                                              final List<Content> contents,
                                              final List<TemplateData> templatesData) {
        if(!model.isQueryModel()) {
            return Collections.emptyList();
        }

        if(ContentQuery.exists(QUERIES_ACTOR, contents)) {
            return ContentQuery.filterByStandard(QUERIES_ACTOR, contents)
                    .filter(Content::isProtocolBased)
                    .map(content -> new QueriesParameter(content.retrieveProtocolQualifiedName(),
                            content.retrieveQualifiedName()))
                    .collect(Collectors.toList());
        }

        return templatesData.stream().filter(data -> data.hasStandard(QUERIES_ACTOR))
                .map(data -> new QueriesParameter(data.parameters())).collect(Collectors.toList());
    }

    public static QueriesParameter from(final CodeGenerationParameter autoDispatchParameter) {
        if(!autoDispatchParameter.hasAny(Label.QUERIES_PROTOCOL)) {
            return null;
        }
        return new QueriesParameter(autoDispatchParameter.relatedParameterValueOf(Label.QUERIES_PROTOCOL),
                autoDispatchParameter.relatedParameterValueOf(Label.QUERIES_ACTOR));
    }

    private QueriesParameter(final TemplateParameters parameters) {
        this(parameters.find(PACKAGE_NAME), parameters.find(QUERIES_NAME),
                parameters.find(PACKAGE_NAME), parameters.find(QUERIES_ACTOR_NAME));
    }

    private QueriesParameter(final String protocolQualifiedName,
                             final String actorQualifiedName) {
        this(ClassFormatter.packageOf(protocolQualifiedName), ClassFormatter.simpleNameOf(protocolQualifiedName),
                ClassFormatter.packageOf(actorQualifiedName), ClassFormatter.simpleNameOf(actorQualifiedName));
    }

    private QueriesParameter(final String protocolPackageName,
                             final String protocolName,
                             final String actorPackageName,
                             final String actorName) {
        this.actorName = actorName;
        this.protocolName = protocolName;
        this.attributeName = ClassFormatter.simpleNameToAttribute(protocolName);
        this.qualifiedNames.addAll(
                Arrays.asList(String.format(QUALIFIED_NAME_PATTERN, protocolPackageName, protocolName),
                        String.format(QUALIFIED_NAME_PATTERN, actorPackageName, actorName)));
    }

    public String getProtocolName() {
        return protocolName;
    }

    public String getActorName() {
        return actorName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public List<String> getQualifiedNames() {
        return qualifiedNames;
    }

}
