// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.persistence;

import io.vlingo.xoom.codegen.content.TypeBasedContentLoader;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.template.TemplateStandard.STATE;

public class StateContentLoader extends TypeBasedContentLoader {

    protected StateContentLoader(final Element annotatedClass,
                                 final ProcessingEnvironment environment) {
        super(annotatedClass, environment);
    }

    @Override
    protected List<TypeElement> retrieveTypes() {
        final StateAdapters stateAdapters =
                annotatedClass.getAnnotation(StateAdapters.class);

        if(stateAdapters == null) {
            return Collections.emptyList();
        }

        return Stream.of(stateAdapters.values())
                .map(adapter -> retrieveType(adapter, anAdapter -> adapter.from()))
                .collect(Collectors.toList());
    }

    @Override
    protected TemplateStandard standard() {
        return STATE;
    }

}
