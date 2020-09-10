// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen;

import io.vlingo.xoom.annotation.initializer.contentloader.ContentLoader;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameters;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateFile;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.storage.DatabaseType;
import io.vlingo.xoom.codegen.template.storage.Model;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.CodeGenerationLocation.EXTERNAL;
import static io.vlingo.xoom.codegen.CodeGenerationLocation.INTERNAL;
import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.storage.DatabaseType.IN_MEMORY;

public class CodeGenerationContext {

    private Filer filer;
    private Element source;
    private final CodeGenerationParameters parameters;
    private final List<Content> contents = new ArrayList<>();
    private final List<TemplateData> templatesData = new ArrayList<>();

    public static CodeGenerationContext empty() {
        return new CodeGenerationContext();
    }

    public static CodeGenerationContext using(final Filer filer, final Element source) {
        return new CodeGenerationContext(filer, source);
    }

    public static CodeGenerationContext with(final Map<Label, String> parameters) {
        return new CodeGenerationContext().on(parameters);
    }

    private CodeGenerationContext() {
        this(null, null);
    }

    private CodeGenerationContext(final Filer filer, final Element source) {
        this.filer = filer;
        this.source = source;
        this.parameters =
                CodeGenerationParameters.from(GENERATION_LOCATION, filer == null ?
                        EXTERNAL.name() : INTERNAL.name());
    }

    public CodeGenerationContext contents(final List<ContentLoader> loaders) {
        loaders.forEach(loader -> loader.load(this));
        return this;
    }

    public CodeGenerationContext with(final Label label, final String value) {
        on(CodeGenerationParameters.from(label, value));
        return this;
    }

    public CodeGenerationContext on(final Map<Label, String> parameters) {
        this.parameters.addAll(parameters);
        return this;
    }

    public CodeGenerationContext on(final CodeGenerationParameters parameters) {
        this.parameters.addAll(parameters);
        return this;
    }

    public <T> T parameterOf(final Label label) {
        return (T) parameterOf(label, value -> value);
    }

    public <T> T parameterOf(final Label label, final Function<String, T> mapper) {
        final String value = parameters.retrieveValue(label);
        return (T) mapper.apply(value);
    }

    public List<TemplateData> templateParametersOf(final TemplateStandard standard) {
        return templatesData.stream().filter(templateData -> templateData.hasStandard(standard))
                .collect(Collectors.toList());
    }

    public void registerTemplateProcessing(final TemplateData templateData, final String text) {
        this.addContent(templateData.standard(), new TemplateFile(this, templateData), text);
        this.templatesData.add(templateData);
    }

    //TODO: Make it private
    public CodeGenerationContext addContent(final TemplateStandard standard,
                                            final TemplateFile file,
                                            final String text) {
        this.contents.add(Content.with(standard, file, filer, source, text));
        return this;
    }

    public CodeGenerationContext addContent(final TemplateStandard standard,
                                            final TypeElement type) {
        this.contents.add(Content.with(standard, type));
        return this;
    }

    public CodeGenerationContext addContent(final TemplateStandard standard,
                                            final TypeElement protocolType,
                                            final TypeElement actorType) {
        this.contents.add(Content.with(standard, protocolType, actorType));
        return this;
    }

    public Map<Model, DatabaseType> databases() {
        if(parameterOf(CQRS, Boolean::valueOf)) {
            return new HashMap<Model, DatabaseType>(){{
                put(Model.COMMAND, parameterOf(COMMAND_MODEL_DATABASE, name -> DatabaseType.getOrDefault(name, IN_MEMORY)));
                put(Model.QUERY, parameterOf(QUERY_MODEL_DATABASE, name -> DatabaseType.getOrDefault(name, IN_MEMORY)));
            }};
        }
        return new HashMap<Model, DatabaseType>(){{
            put(Model.DOMAIN, parameterOf(DATABASE, name -> DatabaseType.getOrDefault(name, IN_MEMORY)));
        }};
    }

    public boolean isInternalGeneration() {
        return parameterOf(GENERATION_LOCATION, CodeGenerationLocation::valueOf).isInternal();
    }

    public Stream<CodeGenerationParameter> parametersOf(final Label label) {
        return parameters.retrieveAll(label);
    }

    public boolean hasParameter(final Label label) {
        return this.parameterOf(label) != null &&
                !this.<String>parameterOf(label).trim().isEmpty();
    }

    public List<Content> contents() {
        return Collections.unmodifiableList(contents);
    }
}
