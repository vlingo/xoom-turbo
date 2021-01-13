// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.exchange;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.template.TemplateFile;

import static io.vlingo.xoom.codegen.template.TemplateStandard.DATA_OBJECT;

public class ContentBuilder {

    public static Content authorDataObjectContent() {
        return Content.with(DATA_OBJECT, new TemplateFile("", "AuthorData.java"), null, null, AUTHOR_DATA_CONTENT_TEXT);
    }

    private static final String AUTHOR_DATA_CONTENT_TEXT =
            "package io.vlingo.xoomapp.infrastructure; \\n" +
                    "public class AuthorData { \\n" +
                    "... \\n" +
                    "}";

}
