// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer.contentloader;

import io.vlingo.lattice.model.sourcing.EventSourced;
import io.vlingo.xoom.annotation.PackageNavigator;
import io.vlingo.xoom.annotation.persistence.Persistence;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AggregateContentLoader extends TypeBasedContentLoader {

    protected AggregateContentLoader(final Element annotatedClass,
                                     final ProcessingEnvironment environment) {
        super(annotatedClass, environment);
    }

    @Override
    protected List<TypeElement> retrieveContentSource() {
        final Persistence persistence = annotatedClass.getAnnotation(Persistence.class);

        if(!persistence.storageType().isJournal()) {
            return Collections.emptyList();
        }

        final String[] allPackages =
                PackageNavigator.from(persistence.basePackage())
                        .retrieveAll().toArray(new String[]{});

        final List<TypeElement> allElements =
                typeRetriever.subclassesOf(EventSourced.class, allPackages)
                        .map(this::toType).collect(Collectors.toList());
        return allElements;
    }

    private TypeElement toType(final TypeMirror typeMirror) {
        return (TypeElement) environment.getTypeUtils().asElement(typeMirror);
    }

    @Override
    protected TemplateStandard standard() {
        return TemplateStandard.AGGREGATE;
    }

}
