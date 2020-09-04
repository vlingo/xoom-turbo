// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.autodispatch;

import io.vlingo.actors.Actor;
import io.vlingo.xoom.annotation.AnnotatedElements;
import io.vlingo.xoom.annotation.Validation;
import org.junit.Test;
import org.mockito.Mockito;

import javax.annotation.processing.ProcessingEnvironment;
import java.lang.annotation.Annotation;

import static org.mockito.Mockito.when;

public class QueriesValidationTest {

    @Test
    public void testThatSingularityValidationPasses() {
        final AnnotatedElements annotatedElements = Mockito.mock(AnnotatedElements.class);
        when(annotatedElements.count(Mockito.eq(Queries.class))).thenReturn(1);
        Validation.singularityValidation().validate(Mockito.mock(ProcessingEnvironment.class),
                Queries.class, annotatedElements);
    }

    private Queries createQueriesAnnotation() {
        return new Queries() {
            @Override
            public Class<?> protocol() {
                return ActorProtocol.class;
            }

            @Override
            public Class<? extends Actor> actor() {
                return null;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Queries.class;
            }
        };
    }

    interface ActorProtocol {
    }
}
