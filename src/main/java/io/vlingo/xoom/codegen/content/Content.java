package io.vlingo.xoom.codegen.content;

import io.vlingo.xoom.codegen.template.TemplateFile;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public abstract class Content {

    public final TemplateStandard standard;

    protected Content(final TemplateStandard standard) {
        this.standard = standard;
    }

    public static Content with(final TemplateStandard standard,
                               final TemplateFile templatefile,
                               final Filer filer,
                               final Element source,
                               final String text) {
        return new TextBasedContent(standard, templatefile, source, filer, text);
    }

    public static Content with(final TemplateStandard standard,
                               final TypeElement type) {
        return new TypeBasedContent(standard, type);
    }

    public static Content with(final TemplateStandard standard,
                               final TypeElement protocolType,
                               final TypeElement actorType) {
        return new ProtocolBasedContent(standard, protocolType, actorType);
    }

    public abstract void create();

    public abstract String retrieveName();

    public abstract String retrievePackage();

    public abstract String retrieveQualifiedName();

    public abstract boolean canWrite();

    public abstract boolean contains(final String term);

    public String retrieveProtocolQualifiedName() {
        throw new UnsupportedOperationException("Content does not have a protocol by default");
    }

    public boolean isProtocolBased() {
        return false;
    }

    public boolean has(final TemplateStandard standard) {
        return this.standard.equals(standard);
    }

    public boolean isNamed(final String name) {
        return retrieveName().equals(name);
    }

}
