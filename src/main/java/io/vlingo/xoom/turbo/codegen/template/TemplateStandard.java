package io.vlingo.xoom.turbo.codegen.template;

public interface TemplateStandard {

  String resolveClassname();

  String resolveClassname(final String name);

  String resolveClassname(final TemplateParameters parameters);

  String resolveClassname(final String name, final TemplateParameters parameters);

  String resolveFilename(final TemplateParameters parameters);

  String resolveFilename(final String name, final TemplateParameters parameters);

  String retrieveTemplateFilename(final TemplateParameters parameters);

}
