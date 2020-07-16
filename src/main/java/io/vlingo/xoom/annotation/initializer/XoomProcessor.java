// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer;

import com.google.auto.service.AutoService;
import io.vlingo.xoom.annotation.AnnotationProcessor;

import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import java.util.Set;

@AutoService(Processor.class)
public class XoomProcessor extends AnnotationProcessor {

    protected void generate(final Set<? extends Element> annotatedElements) {
        XoomValidator.instance().validate(annotatedElements);
        XoomInitializerGenerator.instance().generateFrom(environment, annotatedElements);
    }

    @Override
    public Class<?> annotationClass() {
        return Xoom.class;
    }

}
