// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template;

import io.vlingo.xoom.codegen.content.ClassFormatter;
import io.vlingo.xoom.codegen.parameter.ImportParameter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;

public class TemplateParameters {

    private final Map<String, Object> parameters = new HashMap<>();

    private TemplateParameters() {
        parameters.put(SOURCE_CODE.key, true);
    }

    public static TemplateParameters empty() {
        return new TemplateParameters();
    }

    public static TemplateParameters with(final TemplateParameter parameter, final Object value) {
        return new TemplateParameters().and(parameter, value);
    }

    public TemplateParameters and(final TemplateParameter parameter, final Object value) {
        this.parameters.put(parameter.key, value);
        return this;
    }

    public TemplateParameters andResolve(final TemplateParameter parameter, final Function<TemplateParameters, Object> resolver) {
        this.parameters.put(parameter.key, resolver.apply(this));
        return this;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public TemplateParameters addImport(final String qualifiedClassName) {
        if(this.find(TemplateParameter.IMPORTS) == null) {
            this.and(TemplateParameter.IMPORTS, new HashSet<ImportParameter>());
        }
        if(validateImport(qualifiedClassName)) {
            this.<Set>find(TemplateParameter.IMPORTS).add(new ImportParameter(qualifiedClassName.trim()));
        }
        return this;
    }

    private boolean validateImport(final String qualifiedClassName) {
        if(qualifiedClassName == null || qualifiedClassName.trim().isEmpty()) {
            return false;
        }

        final String classPackage = ClassFormatter.packageOf(qualifiedClassName);

        if(find(PACKAGE_NAME).equals(classPackage)) {
            return false;
        }

        return true;
    }

    public TemplateParameters addImports(final Set<String> qualifiedClassNames) {
        qualifiedClassNames.forEach(this::addImport);
        return this;
    }

    public void convertImportSyntax(final Function<String, String> syntaxResolver){
        if(hasImports()) {
            final Set<ImportParameter> imports = this.find(IMPORTS);
            final Set<String> resolvedImports =
                    imports.stream().map(ImportParameter::getQualifiedClassName)
                            .map(syntaxResolver).collect(Collectors.toSet());
            remove(IMPORTS);
            addImports(resolvedImports);
        }
    }

    @SuppressWarnings("unchecked")
    public boolean hasImport(final String qualifiedName) {
        return ((Set<ImportParameter>) find(IMPORTS)).stream()
                .anyMatch(imp -> imp.matchClass(qualifiedName));
    }

    @SuppressWarnings("unchecked")
    public <T> T find(final TemplateParameter parameter) {
        return (T) this.parameters.get(parameter.key);
    }

    public <T> T find(final TemplateParameter parameter, final T defaultValue) {
        if(!this.parameters.containsKey(parameter.key)) {
            return defaultValue;
        }
        return find(parameter);
    }

    private void remove(final TemplateParameter parameter) {
        this.parameters.remove(parameter.key);
    }

    public Map<String, Object> map() {
        return parameters;
    }

    public boolean has(final TemplateParameter parameter) {
        return parameters.containsKey(parameter.key) &&
                parameters.get(parameter.key) != null &&
                !parameters.get(parameter.key).toString().trim().isEmpty();
    }

    public boolean hasValue(final TemplateParameter parameter, final String value) {
        return has(parameter) && this.parameters.get(parameter.key).equals(value);
    }

    private boolean hasImports() {
        return this.parameters.containsKey(IMPORTS.key);
    }
}
