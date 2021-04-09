// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.storage;

public class DatabaseParameterNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String EXCEPTION_MESSAGE_PATTERN = "%s Database %s not informed";

    public DatabaseParameterNotFoundException(final Model model) {
        this(model, "");
    }

    public DatabaseParameterNotFoundException(final Model model, final String attribute) {
        super(model.isDomainModel() ?
                String.format(EXCEPTION_MESSAGE_PATTERN, "", attribute) :
                String.format(EXCEPTION_MESSAGE_PATTERN, model, attribute));
    }
}
