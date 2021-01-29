// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen;

public class CodeGenerationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CodeGenerationException(final Exception exception) {
        super(exception);
    }

    public CodeGenerationException(final String message) {
        super(message);
    }

}
