// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer;

import com.google.auto.service.AutoService;
import io.vlingo.xoom.annotation.AnnotationProcessor;
import io.vlingo.xoom.annotation.persistence.Persistence;
import io.vlingo.xoom.annotation.persistence.PersistenceValidator;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@AutoService(Processor.class)
public class XoomProcessor extends AnnotationProcessor {

    @Override
    protected void generate(final Map<Class, Set<Element>> annotatedElements) {
        if(annotatedElements.get(Xoom.class).isEmpty()) {
            return;
        }
        XoomValidator.instance().validate(annotatedElements);
        PersistenceValidator.instance().validate(annotatedElements);
        XoomInitializerGenerator.instance().generateFrom(environment, annotatedElements);
    }

    @Override
    public Stream<Class> supportedAnnotationClasses() {
        return Stream.of(Xoom.class, Persistence.class);
    }

}
