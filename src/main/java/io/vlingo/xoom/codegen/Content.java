package io.vlingo.xoom.codegen;

import io.vlingo.xoom.codegen.template.TemplateFile;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Content {

    public final TemplateStandard standard;
    public final File file;
    public final String text;

    private Content(final TemplateStandard standard,
                    final TemplateFile templatefile,
                    final String text) {
        this.text = text;
        this.standard = standard;
        this.file = new File(Paths.get(templatefile.absolutePath(),
                        templatefile.filename()).toString());
    }

    public static Content with(final TemplateStandard standard,
                               final TemplateFile templatefile,
                               final String text) {
        return new Content(standard, templatefile, text);
    }

    public void create() {
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
            Files.write(file.toPath(), text.getBytes());
        } catch (IOException e) {
            throw new CodeGenerationException(e);
        }
    }

    public boolean isAbout(final Object subject) {
        return this.standard.equals(subject);
    }

}
