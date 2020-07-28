// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.TypeBasedContentLoader;
import io.vlingo.xoom.codegen.template.TemplateFile;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.storage.DatabaseType;
import io.vlingo.xoom.codegen.template.storage.ModelClassification;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.*;
import java.util.function.Function;

import static io.vlingo.xoom.codegen.CodeGenerationLocation.EXTERNAL;
import static io.vlingo.xoom.codegen.CodeGenerationLocation.INTERNAL;
import static io.vlingo.xoom.codegen.CodeGenerationParameter.*;

public class CodeGenerationContext {

    private Filer filer;
    private Element source;
    private final List<Content> contents = new ArrayList<>();
    private final Map<CodeGenerationParameter, String> parameters = new HashMap<>();

    public static CodeGenerationContext empty() {
        return new CodeGenerationContext();
    }

    public static CodeGenerationContext using(final Filer filer, final Element source) {
        return new CodeGenerationContext(filer, source);
    }

    public static CodeGenerationContext with(final Map<CodeGenerationParameter, String> parameters) {
        return new CodeGenerationContext().on(parameters);
    }

    private CodeGenerationContext() {
        this(null, null);
    }

    private CodeGenerationContext(final Filer filer, final Element source) {
        this.filer = filer;
        this.source = source;
        parameters.put(GENERATION_LOCATION,
                filer == null ? EXTERNAL.name() : INTERNAL.name());
    }

    public CodeGenerationContext on(final Map<CodeGenerationParameter, String> parameters) {
        this.parameters.putAll(parameters);
        return this;
    }

    public CodeGenerationContext with(final List<TypeBasedContentLoader> loaders) {
        loaders.forEach(loader -> loader.load(this));
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

    public CodeGenerationContext addContent(final TemplateStandard standard,
                           final TemplateFile file,
                           final String text) {
        this.contents.add(Content.with(standard, file, filer, source, text));
        return this;
    }

    public CodeGenerationContext addContent(final TemplateStandard standard,
                           final TypeElement typeElement) {
        this.contents.add(Content.with(standard, typeElement));
        return this;
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

    public boolean isInternalGeneration() {
        return parameterOf(GENERATION_LOCATION, CodeGenerationLocation::valueOf).isInternal();
    }

    public List<Content> contents() {
        return Collections.unmodifiableList(contents);
    }



}
