// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.model.formatting;

import io.vlingo.xoom.codegen.language.Language;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.STATE_FIELD;

public interface AggregateFieldsFormat<T> {

  static <T> T format(final Style style,
                      final Language language,
                      final CodeGenerationParameter aggregate) {
    return format(style, language, aggregate, aggregate.retrieveAllRelated(STATE_FIELD));
  }

  static <T> T format(final Style style,
                      final Language language,
                      final CodeGenerationParameter aggregate,
                      final Stream<CodeGenerationParameter> fields) {
    final Function<Language, AggregateFieldsFormat<?>> instantiator = INSTANTIATORS.get(style);
    return (T) instantiator.apply(language).format(aggregate, fields);
  }

  T format(final CodeGenerationParameter parameter, final Stream<CodeGenerationParameter> fields);

  enum Style {
    ASSIGNMENT, MEMBER_DECLARATION, STATE_BASED_ASSIGNMENT,
    SELF_ALTERNATE_REFERENCE, ALTERNATE_REFERENCE_WITH_DEFAULT_VALUE
  }

  Map<Style, Function<Language, AggregateFieldsFormat<?>>> INSTANTIATORS = Collections.unmodifiableMap(
          new HashMap<Style, Function<Language, AggregateFieldsFormat<?>>>() {{
            put(Style.ASSIGNMENT, lang -> new Constructor());
            put(Style.MEMBER_DECLARATION, lang -> new Member(lang));
            put(Style.STATE_BASED_ASSIGNMENT, lang -> new Constructor("state"));
            put(Style.SELF_ALTERNATE_REFERENCE, lang -> AlternateReference.handlingSelfReferencedFields());
            put(Style.ALTERNATE_REFERENCE_WITH_DEFAULT_VALUE, lang -> AlternateReference.handlingDefaultFieldsValue());
          }});
}
