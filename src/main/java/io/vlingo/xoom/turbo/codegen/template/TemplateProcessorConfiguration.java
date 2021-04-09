// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.codegen.template;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

import java.util.Locale;

import static freemarker.template.Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS;

public class TemplateProcessorConfiguration {

    public final Configuration configuration;

    private static TemplateProcessorConfiguration instance;

    public static TemplateProcessorConfiguration instance() {
        if(instance == null) {
            instance = new TemplateProcessorConfiguration();
        }
        return instance;
    }

    private TemplateProcessorConfiguration() {
        this.configuration = new Configuration(DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        configuration.setClassForTemplateLoading(TemplateProcessor.class, "/");
        configuration.setDefaultEncoding("UTF-8");
        configuration.setLocale(Locale.US);
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

}