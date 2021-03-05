// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.model.formatting;

import io.vlingo.xoom.codegen.language.Language;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.model.MethodScope;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateParameter.DATA_VALUE_OBJECT_ASSIGNMENT;

public class Formatters {

  public static interface Arguments {

      Arguments AGGREGATE_METHOD_INVOCATION = new AggregateMethodInvocation("stage");
      Arguments VALUE_OBJECT_CONSTRUCTOR_INVOCATION = new ValueObjectConstructorInvocation();
      Arguments SIGNATURE_DECLARATION = new SignatureDeclaration();

      default String format(final CodeGenerationParameter parameter) {
          return format(parameter, MethodScope.INSTANCE);
      }

      String format(final CodeGenerationParameter parameter, final MethodScope scope);
  }

  public abstract static class Fields<T> {

    public static <T> T format(final Style style,
                              final Language language,
                              final CodeGenerationParameter parent) {
      if(parent.isLabeled(AGGREGATE)) {
        return format(style, language, parent, parent.retrieveAllRelated(STATE_FIELD));
      } else if(parent.isLabeled(VALUE_OBJECT)) {
        return format(style, language, parent, parent.retrieveAllRelated(VALUE_OBJECT_FIELD));
      }
      throw new UnsupportedOperationException("Unable to format fields from " + parent.label);
    }

    public static <T> T format(final Style style,
                               final Language language,
                               final CodeGenerationParameter parent,
                               final Stream<CodeGenerationParameter> fields) {
      final Function<Language, Fields<?>> instantiator = INSTANTIATORS.get(style);
      return (T) instantiator.apply(language).format(parent, fields);
    }

    abstract T format(final CodeGenerationParameter parameter, final Stream<CodeGenerationParameter> fields);

    public static enum Style {
      ASSIGNMENT, MEMBER_DECLARATION, DATA_OBJECT_MEMBER_DECLARATION, STATE_BASED_DATA_VALUE_OBJECT_ASSIGNMENT,
      STATE_BASED_ASSIGNMENT, SELF_ALTERNATE_REFERENCE, ALTERNATE_REFERENCE_WITH_DEFAULT_VALUE
    }

    private static Map<Style, Function<Language, Fields<?>>> INSTANTIATORS = Collections.unmodifiableMap(
      new HashMap<Style, Function<Language, Fields<?>>>() {{
        put(Style.ASSIGNMENT, lang -> new Constructor());
        put(Style.MEMBER_DECLARATION, lang -> new Member(lang));
        put(Style.DATA_OBJECT_MEMBER_DECLARATION, lang -> new Member(lang, "Data"));
        put(Style.STATE_BASED_ASSIGNMENT, lang -> new Constructor("state"));
        put(Style.STATE_BASED_DATA_VALUE_OBJECT_ASSIGNMENT, lang -> new Constructor("state", DATA_VALUE_OBJECT_ASSIGNMENT));
        put(Style.SELF_ALTERNATE_REFERENCE, lang -> AlternateReference.handlingSelfReferencedFields());
        put(Style.ALTERNATE_REFERENCE_WITH_DEFAULT_VALUE, lang -> AlternateReference.handlingDefaultFieldsValue());
      }});
  }
}
