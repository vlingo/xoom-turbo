package io.vlingo.xoom.codegen.template;


import io.vlingo.xoom.codegen.CodeGenerationSetup;
import io.vlingo.xoom.codegen.template.storage.ModelClassification;
import io.vlingo.xoom.codegen.template.storage.StorageType;

import java.util.function.BiFunction;
import java.util.function.Function;

import static io.vlingo.xoom.codegen.CodeGenerationSetup.*;
import static io.vlingo.xoom.codegen.template.Template.ANNOTATED_BOOTSTRAP;
import static io.vlingo.xoom.codegen.template.Template.DEFAULT_BOOTSTRAP;
import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.storage.StorageType.STATE_STORE;

public enum TemplateStandard {

    AGGREGATE_PROTOCOL(parameters -> Template.AGGREGATE_PROTOCOL.filename),

    AGGREGATE(parameters -> AGGREGATE_TEMPLATES.get(parameters.find(STORAGE_TYPE)),
            (name, parameters) -> name + "Entity"),

    STATE(parameters -> CodeGenerationSetup.STATE_TEMPLATES.get(parameters.find(STORAGE_TYPE)),
            (name, parameters) -> name + "State"),

    ENTITY_DATA(parameters -> Template.ENTITY_DATA.filename,
            (name, parameters) -> name + "Data"),

    REST_RESOURCE(parameters -> Template.REST_RESOURCE.filename,
            (name, parameters) -> name + "Resource"),

    ADAPTER(parameters -> ADAPTER_TEMPLATES.get(parameters.find(STORAGE_TYPE)),
            (name, parameters) -> name + "Adapter"),

    PROJECTION(parameters -> CodeGenerationSetup.PROJECTION_TEMPLATES.get(parameters.find(PROJECTION_TYPE)),
            (name, parameters) -> name + "ProjectionActor"),

    PROJECTION_DISPATCHER_PROVIDER(parameters -> Template.PROJECTION_DISPATCHER_PROVIDER.filename,
            (name, parameters) -> "ProjectionDispatcherProvider"),

    BOOTSTRAP(parameters -> parameters.find(USE_ANNOTATIONS) ?
            ANNOTATED_BOOTSTRAP.filename : DEFAULT_BOOTSTRAP.filename,
            (name, parameters) -> "Bootstrap"),

    DOMAIN_EVENT(parameters -> {
        if (parameters.find(PLACEHOLDER_EVENT)) {
            return Template.PLACEHOLDER_DOMAIN_EVENT.filename;
        }
        return Template.DOMAIN_EVENT.filename;
    }, (name, parameters) -> parameters.find(PLACEHOLDER_EVENT) ? name + "PlaceholderDefined" : name),

    STORAGE_PROVIDER(parameters -> {
        return storeProviderTemplatesFrom(parameters.find(MODEL_CLASSIFICATION))
                .get(parameters.find(STORAGE_TYPE));
    }, (name, parameters) -> {
        final StorageType storageType = parameters.find(STORAGE_TYPE);
        final ModelClassification modelClassification = parameters.find(MODEL_CLASSIFICATION);
        if(modelClassification.isQueryModel()) {
            return STATE_STORE.resolveProviderNameFrom(modelClassification);
        }
        return storageType.resolveProviderNameFrom(modelClassification);
    });

    private static final String FILE_EXTENSION = ".java";

    private final Function<TemplateParameters, String> templateFileRetriever;
    private final BiFunction<String, TemplateParameters, String> nameResolver;

    TemplateStandard(final Function<TemplateParameters, String> templateFileRetriever) {
        this(templateFileRetriever, (name, parameters) -> name);
    }

    TemplateStandard(final Function<TemplateParameters, String> templateFileRetriever,
                     final BiFunction<String, TemplateParameters, String> nameResolver) {
        this.templateFileRetriever = templateFileRetriever;
        this.nameResolver = nameResolver;
    }

    public String retrieveTemplateFilename(final TemplateParameters parameters) {
        return templateFileRetriever.apply(parameters);
    }

    public String resolveClassname(final String name) {
        return resolveClassname(name, null);
    }

    public String resolveClassname(final TemplateParameters parameters) {
        return resolveClassname(null, parameters);
    }

    public String resolveClassname(final String name, final TemplateParameters parameters) {
        return this.nameResolver.apply(name, parameters);
    }

    public String resolveFilename(final TemplateParameters parameters) {
        return resolveFilename(null, parameters);
    }

    public String resolveFilename(final String name, final TemplateParameters parameters) {
        return this.nameResolver.apply(name, parameters) + FILE_EXTENSION;
    }

    public boolean isProjectionDispatcherProvider() {
        return equals(PROJECTION_DISPATCHER_PROVIDER);
    }
}
