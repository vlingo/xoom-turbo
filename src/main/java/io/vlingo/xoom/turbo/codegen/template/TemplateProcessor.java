package io.vlingo.xoom.turbo.codegen.template;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.vlingo.xoom.turbo.codegen.CodeGenerationException;
import io.vlingo.xoom.turbo.codegen.language.Language;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class TemplateProcessor {

    private static TemplateProcessor instance;
    private static final String TEMPLATE_PATH_PATTERN = "codegen/%s/%s.ftl";

    private TemplateProcessor() {
    }

    public static TemplateProcessor instance(){
        if(instance == null) {
            instance = new TemplateProcessor();
        }
        return instance;
    }

    public String process(final Language language, final TemplateData mainTemplateData) {
        mainTemplateData.dependencies().forEach(templateData -> {
            final String outcome =
                    process(language, templateData.standard(), templateData.parameters());

            mainTemplateData.handleDependencyOutcome(templateData.standard(), outcome);
        });

        return process(language, mainTemplateData.standard(), mainTemplateData.parameters());
    }

    private String process(final Language language, final TemplateStandard standard, final TemplateParameters parameters) {
        try {
            final String templateFilename =
                    standard.retrieveTemplateFilename(parameters);

            final String templatePath =
                    String.format(TEMPLATE_PATH_PATTERN, language.templateFolderName(), templateFilename);

            final Template template =
                    TemplateProcessorConfiguration.instance()
                            .configuration.getTemplate(templatePath);

            final Writer writer = new StringWriter();
            template.process(parameters.map(), writer);
            return writer.toString();
        } catch (final IOException | TemplateException exception) {
            throw new CodeGenerationException(exception);
        }
    }

}
