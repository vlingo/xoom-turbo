// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.actors;

public class PropertiesLoadingException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public PropertiesLoadingException(final String message) {
        super(message);
    }

    public PropertiesLoadingException(final String message, final Exception cause) {
        super(message, cause);
    }

}
