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

public class QueriesValidation implements Validation {

    @Override
    public void validate(final ProcessingEnvironment processingEnvironment,
                         final Class annotation,
                         final AnnotatedElements annotatedElements) {
        annotatedElements.elementsWith(Queries.class).forEach(element -> {
            final Queries queries = element.getAnnotation(Queries.class);
            final TypeRetriever retriever = TypeRetriever.with(processingEnvironment);
            if(!retriever.isAnInterface(queries, Void -> queries.protocol())){
                throw new ProcessingAnnotationException(
                        String.format("Protocol value to Queries annotation must be an interface, class informed: %s",
                                retriever.getClassName(queries,
                                        Void -> queries.protocol())));
            }
        });
    }
}
