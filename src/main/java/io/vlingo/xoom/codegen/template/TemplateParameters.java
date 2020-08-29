// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template;

import io.vlingo.xoom.codegen.file.ImportParameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TemplateParameters {

    private final Map<String, Object> parameters = new HashMap<>();

    private TemplateParameters() {
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

    public TemplateParameters addImport(final String qualifiedClassName) {
        if(this.find(TemplateParameter.IMPORTS) == null) {
            this.and(TemplateParameter.IMPORTS, new ArrayList<ImportParameter>());
        }
        if(qualifiedClassName != null && !qualifiedClassName.trim().isEmpty()) {
            this.<List>find(TemplateParameter.IMPORTS).add(new ImportParameter(qualifiedClassName.trim()));
        }
        return this;
    }

    public TemplateParameters addImports(final List<String> qualifiedClassNames) {
        qualifiedClassNames.forEach(this::addImport);
        return this;
    }

    public <T> T find(final TemplateParameter parameter) {
        return (T) this.parameters.get(parameter.key);
    }

    public <T> T find(final TemplateParameter parameter, final T defaultValue) {
        if(!this.parameters.containsKey(parameter.key)) {
            return defaultValue;
        }
        return find(parameter);
    }

    public Map<String, Object> map() {
        return parameters;
    }


}
