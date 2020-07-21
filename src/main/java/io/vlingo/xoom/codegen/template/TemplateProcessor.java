package io.vlingo.xoom.codegen.template;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import io.vlingo.xoom.codegen.CodeGenerationException;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;

import static freemarker.template.Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS;

public class TemplateProcessor {

    private static TemplateProcessor INSTANCE;
    private static final String TEMPLATE_PATH_PATTERN = "codegen/%s.ftl";

    private TemplateProcessor() {
    }

    public static TemplateProcessor instance(){
        if(INSTANCE == null) {
            INSTANCE = new TemplateProcessor();
        }
        return INSTANCE;
    }

    public String process(final TemplateData templateData) {
        return process(templateData.standard(), templateData.parameters());
    }

    public String process(final TemplateStandard standard, final TemplateParameters parameters) {
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

        configuration.setClassForTemplateLoading(TemplateProcessor.class, "/");
        configuration.setDefaultEncoding("UTF-8");
        configuration.setLocale(Locale.US);
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        return configuration;
    }

}
