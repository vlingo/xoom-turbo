// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.schemata;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;

public class SchemaParameter {

    public final String reference;
    public final String file;

    public SchemaParameter(final CodeGenerationParameter schema) {
        if(!schema.isLabeled(Label.SCHEMA)) {
            throw new IllegalArgumentException("A schema parameter is expected.");
        }
        this.reference = schema.value;
        this.file = null;
    }

    public SchemaParameter(final String schemaGroup,
                           final CodeGenerationParameter domainEvent) {
        if(!domainEvent.isLabeled(Label.DOMAIN_EVENT)) {
            throw new IllegalArgumentException("A Domain Event parameter is expected.");
        }
        this.reference = String.format("%s:%s:0.0.1", schemaGroup, domainEvent.value) ;
        this.file = domainEvent.value + ".vss";
    }

    public String getReference() {
        return reference;
    }

    public String getFile() {
        return file;
    }

}
