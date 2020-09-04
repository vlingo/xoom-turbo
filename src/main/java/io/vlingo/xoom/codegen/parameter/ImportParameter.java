// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.parameter;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImportParameter {

    private final String qualifiedClassName;

    public ImportParameter(final String qualifiedClassName) {
        this.qualifiedClassName = qualifiedClassName;
    }

    public static Set<ImportParameter> of(final String ...qualifiedClassNames) {
        return of(Stream.of(qualifiedClassNames));
    }

    public static Set<ImportParameter> of(final List<String> ...qualifiedNames) {
        return of(Stream.of(qualifiedNames).flatMap(Collection::stream));
    }

    public static Set<ImportParameter> of(final Stream<String> stateQualifiedNames) {
        return stateQualifiedNames.map(ImportParameter::new).collect(Collectors.toSet());
    }

    public String getQualifiedClassName() {
        return qualifiedClassName;
    }

    public boolean matchClass(final String qualifiedClassName) {
        return this.qualifiedClassName.equals(qualifiedClassName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImportParameter that = (ImportParameter) o;
        return qualifiedClassName.equals(that.getQualifiedClassName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifiedClassName);
    }
}
