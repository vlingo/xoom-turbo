// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer.contentloader;

import io.vlingo.xoom.annotation.TypeRetriever;
import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.List;

public abstract class TypeBasedContentLoader {

    protected final Element annotatedClass;
    protected final TypeRetriever typeRetriever;
    protected final ProcessingEnvironment environment;

    protected TypeBasedContentLoader(final Element annotatedClass,
                                     final ProcessingEnvironment environment) {
        this.environment = environment;
        this.annotatedClass = annotatedClass;
        this.typeRetriever = TypeRetriever.with(environment);
    }

    public void load(final CodeGenerationContext context) {
        this.retrieveTypes()
                .forEach(typeElement -> context.addContent(standard(), typeElement));
    }

    protected abstract List<TypeElement> retrieveTypes();

    protected abstract TemplateStandard standard();

}
