// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.content;

import io.vlingo.xoom.codegen.template.TemplateStandard;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class ContentQuery {

    public static boolean exists(final TemplateStandard standard, final List<Content> contents) {
        return filterByStandard(standard, contents).findAny().isPresent();
    }

    public static Set<String> findClassNames(final TemplateStandard standard, final List<Content> contents) {
        return filterByStandard(standard, contents)
                .map(Content::retrieveName)
                .collect(Collectors.toSet());
    }

    public static Set<String> findClassNames(final List<Content> contents, final TemplateStandard ...standards) {
        return Stream.of(standards).flatMap(standard -> findClassNames(standard, contents)
                .stream()).collect(Collectors.toSet());
    }

    public static Set<String> findClassNames(final TemplateStandard standard, final String packageName, final List<Content> contents) {
        return filterByStandard(standard, contents)
                .filter(content -> content.retrievePackage().equals(packageName))
                .map(content -> content.retrieveName())
                .collect(Collectors.toSet());
    }

    public static Set<String> findFullyQualifiedClassNames(final List<Content> contents, final TemplateStandard ...standards) {
        return Arrays.asList(standards).stream()
                .flatMap(standard -> findFullyQualifiedClassNames(standard, contents).stream())
                .collect(Collectors.toSet());
    }

    public static String findFullyQualifiedClassName(final TemplateStandard standard, final String className, final List<Content> contents) {
        return findFullyQualifiedClassNames(standard, contents).stream()
                .filter(qualifiedClassName -> qualifiedClassName.endsWith("." + className))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unable to find class with name " + className));
    }

    public static Set<String> findFullyQualifiedClassNames(final TemplateStandard standard, final List<Content> contents) {
        return filterByStandard(standard, contents).map(Content::retrieveQualifiedName).collect(toSet());
    }

    public static String findPackage(final TemplateStandard standard, final List<Content> contents) {
        return filterByStandard(standard, contents).map(Content::retrievePackage).findAny().orElse("");
    }

    public static String findPackage(final TemplateStandard standard, final String className, final List<Content> contents) {
        return filterByStandard(standard, contents)
                .filter(content -> content.retrieveName().equals(className))
                .map(Content::retrievePackage).findAny().orElse("");
    }

    public static Stream<Content> filterByStandard(final TemplateStandard standard, final List<Content> contents) {
        return contents.stream().filter(content -> content.has(standard));
    }

}