// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.autodispatch;

import io.vlingo.xoom.annotation.AnnotatedElements;
import io.vlingo.xoom.annotation.ProcessingAnnotationException;
import io.vlingo.xoom.annotation.TypeRetriever;
import io.vlingo.xoom.annotation.Validation;

import javax.annotation.processing.ProcessingEnvironment;

public class ActorsValidation implements Validation {

    @Override
    public void validate(final ProcessingEnvironment processingEnvironment, final Class annotation, final AnnotatedElements annotatedElements) {
        annotatedElements.elementsWith(Queries.class).forEach(element -> {
            final Queries queries = element.getAnnotation(Queries.class);
            final boolean anInterface = TypeRetriever.with(processingEnvironment).from(queries, ((retriever) -> queries.protocol())).getKind().isInterface();
            if(!anInterface){
                throw new ProcessingAnnotationException(String.format("Protocol value must be an interface"));
            }
        });
    }
}
