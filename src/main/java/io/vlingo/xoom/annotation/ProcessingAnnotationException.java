// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation;

import javax.lang.model.element.Element;

public class ProcessingAnnotationException extends RuntimeException {

    public final Element element;

    public ProcessingAnnotationException(final String message) {
        this(null, message);
    }

    public ProcessingAnnotationException(final Element element, final String message) {
        super(message);
        this.element = element;
    }

    public ProcessingAnnotationException(final Exception exception) {
        this(null, exception.getMessage());
    }

}
