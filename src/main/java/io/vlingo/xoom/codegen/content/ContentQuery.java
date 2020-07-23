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
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ContentQuery {

     public static boolean exists(final TemplateStandard standard, final List<Content> contents) {
        return filterBySubject(standard, contents).findAny().isPresent();
    }

    public static List<String> findClassNames(final TemplateStandard standard, final List<Content> contents) {
        return filterBySubject(standard, contents)
                .map(Content::retrieveClassName)
                .collect(toList());
    }

    public static List<String> findFullyQualifiedClassNames(final List<Content> contents, final TemplateStandard ...standards) {
        return Arrays.asList(standards).stream()
                .flatMap(standard -> findFullyQualifiedClassNames(standard, contents).stream())
                .collect(toList());
    }

    public static List<String> findFullyQualifiedClassNames(final TemplateStandard standard, final List<Content> contents) {
        return filterBySubject(standard, contents).map(Content::retrieveFullyQualifiedName).collect(toList());
    }

    public static String findPackage(final TemplateStandard standard, final List<Content> contents) {
        return filterBySubject(standard, contents).map(Content::retrievePackage).findAny().orElse("");
    }

    public static String findFullyQualifiedClassName(final TemplateStandard standard, final String className, final List<Content> contents) {
        return findFullyQualifiedClassNames(standard, contents).stream()
                .filter(qualifiedClassName -> qualifiedClassName.endsWith("." + className))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    private static Stream<Content> filterBySubject(final TemplateStandard standard, final List<Content> contents) {
        return contents.stream().filter(content -> content.has(standard));
    }

}