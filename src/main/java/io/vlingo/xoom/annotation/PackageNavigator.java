// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PackageNavigator {

    private final String basePackage;

    private static final String[] SOURCE_FOLDER_PATH = new String[]{"src", "main", "java"};

    public static PackageNavigator from(final String basePackage) {
        return new PackageNavigator(basePackage);
    }

    private PackageNavigator(final String basePackage) {
        this.basePackage = basePackage;
    }

    private String findNextPackage(final String currentPackage,
                                   final Set<String> knownPackages) {
        final Predicate<String> skipKnownPackages = aPackage -> !knownPackages.contains(aPackage);
        return retrieveChild(currentPackage).stream().filter(skipKnownPackages).findFirst().orElse(basePackage);
    }

    private Path resolvePackagePath(final String packageName) {
        final String projectPath = System.getProperty("user.dir");
        return Paths.get(projectPath, ArrayUtils.addAll(SOURCE_FOLDER_PATH, packageName.split("\\.")));
    }

    private Set<String> retrieveChild(final String parentPackage) {
        final Path currentPackagePath = resolvePackagePath(parentPackage);
        return Stream.of(currentPackagePath.toFile().listFiles(File::isDirectory))
                .map(dir -> parentPackage + "." + dir.getName())
                .collect(Collectors.toSet());
    }

    public Set<String> retrieveAll() {
        String nextPackage = null;
        String currentPackage = null;
        final Set<String> packages = new HashSet<>();
        while (currentPackage == null || !nextPackage.equals(currentPackage)) {
            currentPackage = nextPackage == null ? this.basePackage : nextPackage;
            packages.add(currentPackage);
            nextPackage = findNextPackage(currentPackage, packages);
        }
        return packages;
    }

}
