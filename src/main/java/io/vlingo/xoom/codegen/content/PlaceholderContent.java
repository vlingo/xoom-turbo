// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.content;

import io.vlingo.xoom.codegen.template.TemplateStandard;

public class PlaceholderContent extends Content {

    private final String packageName;
    private final String className;

    protected PlaceholderContent(final TemplateStandard standard,
                                 final String packageName,
                                 final String className) {
        super(standard);
        this.packageName = packageName;
        this.className = className;
    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Placeholder Content is read-only");
    }

    @Override
    public String retrieveClassName() {
        return className;
    }

    @Override
    public String retrievePackage() {
        return packageName;
    }

    @Override
    public String retrieveQualifiedName() {
        return ClassFormatter.qualifiedNameOf(packageName, className);
    }

    @Override
    public boolean canWrite() {
        return false;
    }

    @Override
    public boolean contains(final String term) {
        return false;
    }

}
