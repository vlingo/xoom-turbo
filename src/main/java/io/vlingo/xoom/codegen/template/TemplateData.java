// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class TemplateData {

    private final List<TemplateData> dependencies = new ArrayList<>();

    public abstract TemplateParameters parameters();

    public abstract TemplateStandard standard();

    protected void dependOn(final TemplateData templateData) {
        this.dependencies.add(templateData);
    }

    public void handleDependencyOutcome(final TemplateStandard standard, final String outcome) {
        throw new UnsupportedOperationException("Unable to handle dependency outcome");
    }

    public String filename() {
        return standard().resolveFilename(parameters());
    }

    public boolean hasStandard(final TemplateStandard standard) {
        return standard().equals(standard);
    }

    public List<TemplateData> dependencies() {
        return Collections.unmodifiableList(dependencies);
    }

}
