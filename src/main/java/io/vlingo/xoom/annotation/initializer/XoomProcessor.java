// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer;

import com.google.auto.service.AutoService;
import io.vlingo.xoom.annotation.AnnotatedElements;
import io.vlingo.xoom.annotation.AnnotationProcessor;
import io.vlingo.xoom.annotation.autodispatch.AutoDispatchValidator;
import io.vlingo.xoom.annotation.autodispatch.Model;
import io.vlingo.xoom.annotation.autodispatch.Queries;
import io.vlingo.xoom.annotation.autodispatch.Route;
import io.vlingo.xoom.annotation.persistence.Persistence;
import io.vlingo.xoom.annotation.persistence.PersistenceValidator;

import javax.annotation.processing.Processor;
import java.util.stream.Stream;

@AutoService(Processor.class)
public class XoomProcessor extends AnnotationProcessor {

    @Override
    protected void generate(final AnnotatedElements annotatedElements) {
        if(annotatedElements.elementsWith(Xoom.class).isEmpty()) {
            return;
        }
        XoomValidator.instance().validate(environment, annotatedElements);
        PersistenceValidator.instance().validate(environment, annotatedElements);
        AutoDispatchValidator.instance().validate(environment, annotatedElements);
        XoomInitializerGenerator.instance().generateFrom(environment, annotatedElements);
    }

    @Override
    public Stream<Class> supportedAnnotationClasses() {
        return Stream.of(Xoom.class, Persistence.class, Route.class, Model.class, Queries.class);
    }

}
