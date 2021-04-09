// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.codegen.content;

import io.vlingo.xoom.turbo.codegen.template.TemplateStandard;

import javax.lang.model.element.TypeElement;

public class TypeBasedContent extends Content {

    public final TypeElement contentType;

    protected TypeBasedContent(final TemplateStandard standard,
                               final TypeElement contentType) {
        super(standard);
        this.contentType = contentType;
    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Type Based Content is read-only");
    }

    @Override
    public String retrieveName() {
        return contentType.getSimpleName().toString();
    }

    @Override
    public String retrievePackage() {
        return CodeElementFormatter.packageOf(retrieveQualifiedName());
    }

    @Override
    public String retrieveQualifiedName() {
        return contentType.getQualifiedName().toString();
    }

    @Override
    public boolean canWrite() {
        return false;
    }

    @Override
    public boolean contains(final String term) {
        throw new UnsupportedOperationException("Unable to search on TypeBasedContent");
    }

}
