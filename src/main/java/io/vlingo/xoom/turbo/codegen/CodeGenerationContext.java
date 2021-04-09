// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.codegen;

import io.vlingo.xoom.turbo.annotation.initializer.contentloader.ContentLoader;
import io.vlingo.xoom.turbo.codegen.content.Content;
import io.vlingo.xoom.turbo.codegen.language.Language;
import io.vlingo.xoom.turbo.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.turbo.codegen.parameter.CodeGenerationParameters;
import io.vlingo.xoom.turbo.codegen.parameter.Label;
import io.vlingo.xoom.turbo.codegen.template.OutputFile;
import io.vlingo.xoom.turbo.codegen.template.TemplateData;
import io.vlingo.xoom.turbo.codegen.template.TemplateStandard;
import io.vlingo.xoom.turbo.codegen.template.storage.DatabaseType;
import io.vlingo.xoom.turbo.codegen.template.storage.Model;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.turbo.codegen.CodeGenerationLocation.EXTERNAL;
import static io.vlingo.xoom.turbo.codegen.CodeGenerationLocation.INTERNAL;
import static io.vlingo.xoom.turbo.codegen.parameter.Label.*;
import static io.vlingo.xoom.turbo.codegen.template.storage.DatabaseType.IN_MEMORY;

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

    public static CodeGenerationContext with(final CodeGenerationParameters parameters) {
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

    @SuppressWarnings("rawtypes")
    public CodeGenerationContext contents(final List<ContentLoader> loaders) {
        loaders.stream().filter(ContentLoader::shouldLoad).forEach(loader -> loader.load(this));
        return this;
    }

    public CodeGenerationContext contents(final Content ...contents) {
        this.contents.addAll(Arrays.asList(contents));
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

    @SuppressWarnings("unchecked")
    public <T> T parameterOf(final Label label) {
        return (T) parameterOf(label, value -> value);
    }

    public <T> T parameterOf(final Label label, final Function<String, T> mapper) {
        final String value = parameters.retrieveValue(label);
        return mapper.apply(value);
    }

    public List<TemplateData> templateParametersOf(final TemplateStandard standard) {
        return templatesData.stream().filter(templateData -> templateData.hasStandard(standard))
                .collect(Collectors.toList());
    }

    public void registerTemplateProcessing(final Language language, final TemplateData templateData, final String text) {
        this.addContent(templateData.standard(), new OutputFile(this, templateData, language), text);
        this.templatesData.add(templateData);
    }

    //TODO: Make it private
    public CodeGenerationContext addContent(final TemplateStandard standard,
                                            final OutputFile file,
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

    public Content findContent(final TemplateStandard standard, final String contentName) {
        return contents.stream().filter(content -> content.has(standard) && content.isNamed(contentName)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unable to find content " + standard + " - " + contentName));
    }

    public List<Content> contents() {
        return Collections.unmodifiableList(contents);
    }

    public CodeGenerationParameters parameters() {
        return parameters;
    }

    public Language language() {
        if(hasParameter(LANGUAGE)) {
            return parameterOf(Label.LANGUAGE, Language::valueOf);
        }
        return Language.findDefault();
    }

    @SuppressWarnings("serial")
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
}
