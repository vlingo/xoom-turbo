// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen;

import io.vlingo.xoom.codegen.template.TemplateFile;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.storage.DatabaseType;
import io.vlingo.xoom.codegen.template.storage.ModelClassification;

import java.util.*;
import java.util.function.Function;

import static io.vlingo.xoom.codegen.CodeGenerationLocation.EXTERNAL;
import static io.vlingo.xoom.codegen.CodeGenerationLocation.INTERNAL;
import static io.vlingo.xoom.codegen.CodeGenerationParameter.*;

public class CodeGenerationContext {

    private String internalRequesterPath;
    private final List<Content> contents = new ArrayList<>();
    private final Map<CodeGenerationParameter, String> parameters = new HashMap<>();

    public static CodeGenerationContext empty() {
        return new CodeGenerationContext();
    }

    public static CodeGenerationContext from(final String internalRequesterPath) {
        return new CodeGenerationContext(internalRequesterPath);
    }

    public static CodeGenerationContext with(final Map<CodeGenerationParameter, String> parameters) {
        return new CodeGenerationContext().onParameters(parameters);
    }

    private CodeGenerationContext() {
        this(null);
    }

    private CodeGenerationContext(final String internalRequesterPath) {
        this.internalRequesterPath = internalRequesterPath;

        parameters.put(GENERATION_LOCATION,
                internalRequesterPath == null ? EXTERNAL.name() : INTERNAL.name());
    }

    public CodeGenerationContext onParameters(final Map<CodeGenerationParameter, String> parameters) {
        this.parameters.putAll(parameters);
        return this;
    }

    public CodeGenerationContext with(final CodeGenerationParameter parameter, final String value) {
        this.parameters.put(parameter, value);
        return this;
    }

    public <T> T parameterOf(final CodeGenerationParameter codeGenerationParameter) {
        return (T) parameterOf(codeGenerationParameter, value -> value);
    }

    public <T> T parameterOf(final CodeGenerationParameter codeGenerationParameter, final Function<String, T> mapper) {
        final String value = parameters.get(codeGenerationParameter);
        return (T) mapper.apply(value);
    }

    public void addContent(final TemplateStandard standard,
                           final TemplateFile file,
                           final String text) {
        this.contents.add(Content.with(standard, file, text));
    }

    public boolean hasParameter(final CodeGenerationParameter codeGenerationParameter) {
        return this.parameterOf(codeGenerationParameter) != null &&
                !this.<String>parameterOf(codeGenerationParameter).trim().isEmpty();
    }

    public Map<ModelClassification, DatabaseType> databases() {
        if(parameterOf(CQRS, Boolean::valueOf)) {
            return new HashMap<ModelClassification, DatabaseType>(){{
                put(ModelClassification.COMMAND, parameterOf(COMMAND_MODEL_DATABASE, DatabaseType::valueOf));
                put(ModelClassification.QUERY, parameterOf(QUERY_MODEL_DATABASE, DatabaseType::valueOf));
            }};
        }
        return new HashMap<ModelClassification, DatabaseType>(){{
            put(ModelClassification.SINGLE, parameterOf(DATABASE, DatabaseType::valueOf));
        }};
    }

    public String internalRequesterPath() {
        return internalRequesterPath;
    }

    public List<Content> contents() {
        return Collections.unmodifiableList(contents);
    }

}
