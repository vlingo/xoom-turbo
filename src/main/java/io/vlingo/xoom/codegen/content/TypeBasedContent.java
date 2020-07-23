// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.content;

import io.vlingo.xoom.codegen.template.TemplateStandard;

import javax.lang.model.element.TypeElement;

public class TypeBasedContent extends Content {

    public final TypeElement typeElement;

    protected TypeBasedContent(final TemplateStandard standard, final TypeElement typeElement) {
        super(standard);
        this.typeElement = typeElement;
    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Class Based Content is read-only");
    }

    @Override
    public String retrieveClassName() {
        return typeElement.getSimpleName().toString();
    }

    @Override
    public String retrievePackage() {
        final String qualifiedName = retrieveFullyQualifiedName();
        return qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
    }

    @Override
    public String retrieveFullyQualifiedName() {
        return typeElement.getQualifiedName().toString();
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
