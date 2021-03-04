// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NotFileFilter;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.io.filefilter.DirectoryFileFilter.DIRECTORY;
import static org.apache.commons.io.filefilter.TrueFileFilter.INSTANCE;

public class PackageNavigator {

    private final String basePackage;

    private static final String[] SOURCE_FOLDER_PATH = new String[]{"src", "main", "java"};

    public static PackageNavigator from(final String basePackage) {
        return new PackageNavigator(basePackage);
    }

    private PackageNavigator(final String basePackage) {
        this.basePackage = basePackage;
    }

    private Path resolvePackagePath(final String packageName) {
        final String sourceFolderPath = resolveSourceFolderPath().toString();
        return Paths.get(sourceFolderPath, packageName.split("\\."));
    }

    private Path resolveSourceFolderPath() {
        final String projectPath = System.getProperty("user.dir");
        return Paths.get(projectPath, SOURCE_FOLDER_PATH);
    }

    public Set<String> retrieveAll() {
        final File basePackageDirectory = resolvePackagePath(this.basePackage).toFile();
        final int sourceFolderPathLength = resolveSourceFolderPath().toString().length();
        final Function<String, String> rootPathRemover = path -> path.substring(sourceFolderPathLength + 1);
        final Function<String, String> packageFormatter = path -> path.replaceAll("(\\\\|/)", ".");
        return FileUtils.listFilesAndDirs(basePackageDirectory, new NotFileFilter(INSTANCE), DIRECTORY).stream()
                .map(File::getAbsolutePath).map(rootPathRemover).map(packageFormatter)
                .collect(Collectors.toSet());
    }

}
