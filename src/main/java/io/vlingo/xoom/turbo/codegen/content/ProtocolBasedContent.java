// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.codegen.content;

import io.vlingo.xoom.turbo.codegen.template.TemplateStandard;

import javax.lang.model.element.TypeElement;

public class ProtocolBasedContent extends TypeBasedContent {

    public final TypeElement contentProtocolType;

    protected ProtocolBasedContent(final TemplateStandard standard,
                                   final TypeElement contentProtocolType,
                                   final TypeElement contentType) {
        super(standard, contentType);
        this.contentProtocolType = contentProtocolType;
    }

    @Override
    public String retrieveProtocolQualifiedName() {
        return contentProtocolType.getQualifiedName().toString();
    }

    @Override
    public boolean isProtocolBased() {
        return true;
    }

}
