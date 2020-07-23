// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom;

import java.util.function.Predicate;
import java.util.stream.Stream;

public enum OperatingSystem {

    WINDOWS(osName -> osName.contains("Windows")),
    OTHER(osName -> !osName.contains("Windows"));

    private final Predicate<String> evaluator;

    OperatingSystem(Predicate<String> evaluator) {
        this.evaluator = evaluator;
    }

    public static OperatingSystem detect() {
        final String osName = System.getProperty("os.name");
        final Predicate<OperatingSystem> matcher = os -> os.matchName(osName);
        return Stream.of(values()).filter(matcher).findFirst().get();
    }

    private boolean matchName(final String operatingSystemName) {
        return evaluator.test(operatingSystemName);
    }

    public boolean isWindows() {
        return equals(WINDOWS);
    }

}
