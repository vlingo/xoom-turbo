// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.model.formatting;

import io.vlingo.xoom.codegen.language.Language;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.model.FieldDetail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.language.Language.JAVA;
import static io.vlingo.xoom.codegen.language.Language.KOTLIN;

public class Member extends Formatters.Fields<List<String>> {

    private final BiFunction<String, String, String> declarationResolver;

    Member(final Language language) {
        if(!RESOLVERS.containsKey(language)) {
            throw new IllegalArgumentException("Unable to format members on " + language);
        }
        this.declarationResolver = RESOLVERS.get(language);

    }

    @Override
    public List<String> format(final CodeGenerationParameter aggregate,
                               final Stream<CodeGenerationParameter> fields) {
        return fields.map(field -> {
            final String fieldType = FieldDetail.typeOf(aggregate, field.value);
            return declarationResolver.apply(fieldType, field.value);
        }).collect(Collectors.toList());
    }

    private static final Map<Language, BiFunction<String, String, String>> RESOLVERS =
            new HashMap<Language, BiFunction<String, String, String>>() {{
                put(JAVA, (fieldType, fieldName) -> String.format("public final %s %s;", fieldType, fieldName));
                put(KOTLIN, (fieldType, fieldName) -> String.format("val %s: %s;", fieldName, fieldType));
            }};

}