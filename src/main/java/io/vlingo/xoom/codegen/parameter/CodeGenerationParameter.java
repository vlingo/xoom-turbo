// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.parameter;

public class CodeGenerationParameter {

    public final Category category;
    public final Label label;
    public final Parent parent;
    public final String value;

    public CodeGenerationParameter(final Category category,
                                   final Label label,
                                   final String value) {
        this(category, Parent.none(), label, value);
    }

    public CodeGenerationParameter(final Category category,
                                   final Label label,
                                   final String parentName,
                                   final String value) {
        this(category, Parent.identifiedBy(parentName), label, value);
    }

    public CodeGenerationParameter(final Category category,
                                   final Parent parent,
                                   final Label label,
                                   final String value) {
        this.category = category;
        this.label = label;
        this.parent = parent;
        this.value = value;
    }

    public boolean has(final Category category, final Label label) {
        return this.category.equals(category) && this.label.equals(label);
    }

    public boolean has(final Category category, final Parent parent, final Label label) {
        return this.parent.equals(parent) && has(category, label);
    }

}
