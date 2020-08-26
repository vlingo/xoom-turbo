// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.parameter;

public class Parent {

    public final String name;

    private Parent(final String name) {
        this.name = name;
    }

    public static Parent identifiedBy(final String name) {
        return new Parent(name);
    }

    public static Parent none() {
        return identifiedBy(null);
    }

    public boolean isNamed() {
        return name != null;
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null || other.getClass() != getClass()) {
            return false;
        } else if (this == other) {
            return true;
        }

        final Parent otherParent = (Parent) other;

        if(!this.isNamed() && !otherParent.isNamed()) {
            return true;
        }

        return this.isNamed() &&
                otherParent.isNamed() &&
                this.name.equals(otherParent.name);
    }
}
