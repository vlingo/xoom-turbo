package io.vlingo.xoom.codegen;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;

import static freemarker.template.Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS;

public class CodeTemplateProcessor {

    private static CodeTemplateProcessor INSTANCE;
    private static final String TEMPLATE_PATH_PATTERN = "codegen/%s.ftl";

    private CodeTemplateProcessor() {
    }

    public static CodeTemplateProcessor instance(){
        if(INSTANCE == null) {
            INSTANCE = new CodeTemplateProcessor();
        }
        return INSTANCE;
    }

    public String process(final TemplateData templateData) {
        return process(templateData.standard(), templateData.parameters());
    }

    public String process(final CodeTemplateStandard standard, final CodeTemplateParameters parameters) {
        try {
            final String templateFilename =
                    standard.retrieveTemplateFilename(parameters);

            final String templatePath =
                    String.format(TEMPLATE_PATH_PATTERN, templateFilename);

            final Template template =
                    freeMarkerSettings().getTemplate(templatePath);

            final Writer writer = new StringWriter();
            template.process(parameters.map(), writer);
            return writer.toString();
        } catch (final IOException | TemplateException exception) {
            throw new CodeGenerationException(exception);
        }
    }

    public static Configuration freeMarkerSettings() {
        final Configuration configuration =
                new Configuration(DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

        configuration.setClassForTemplateLoading(CodeTemplateProcessor.class, "/");
        configuration.setDefaultEncoding("UTF-8");
        configuration.setLocale(Locale.US);
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        return configuration;
    }

}
