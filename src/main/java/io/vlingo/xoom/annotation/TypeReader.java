// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation;

import org.apache.commons.io.IOUtils;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TypeReader {

    private final static String DEFAULT_MATCH_PATTERN = " %s ";
    private final static String VARIABLE_MATCH_PATTERN = " %s=";

    private final TypeElement type;
    private final String sourceCode;

    public static TypeReader from(final ProcessingEnvironment environment,
                                  final TypeElement typeElement) {
        return new TypeReader(environment, typeElement);
    }

    private TypeReader(final ProcessingEnvironment environment,
                       final TypeElement type) {
        this.type = type;
        this.sourceCode = readSourceCode(environment.getFiler(), type);
    }

    public String findMemberValue(final VariableElement member) {
        final String memberName = member.getSimpleName().toString();
        if(!hasMember(memberName)) {
            throw invalidMemberArgumentException(memberName);
        }
        return extractMemberValue(memberName);
    }

    public String findMemberValue(final String memberName) {
        return findMembers().stream().map(member -> member.getSimpleName().toString())
                .filter(name -> name.equals(memberName)).map(member -> extractMemberValue(memberName))
                .findFirst().orElseThrow(() -> invalidMemberArgumentException(memberName));
    }

    public List<VariableElement> findMembers() {
        return type.getEnclosedElements().stream()
                .filter(element -> element instanceof VariableElement)
                .map(element -> (VariableElement) element)
                .collect(Collectors.toList());
    }

    private String extractMemberValue(final String memberName) {
        final int elementIndex = codeElementIndex(memberName, DEFAULT_MATCH_PATTERN, VARIABLE_MATCH_PATTERN);
        final int valueIndex = elementIndex + memberName.length() + 1;
        final String codeSlice = sourceCode.substring(valueIndex).replaceAll("=", "").trim();
        return codeSlice.substring(0, codeSlice.indexOf(";")).trim();
    }

    private String readSourceCode(final Filer filer,
                                  final TypeElement typeElement) {
        try {
            final InputStream stream =
                    ClassFile.from(filer, typeElement).openInputStream();

            return IOUtils.toString(stream, UTF_8.name()).replaceAll("\r\n", " ");
        } catch (final IOException e) {
            throw new ProcessingAnnotationException(e);
        }
    }

    private IllegalArgumentException invalidMemberArgumentException(final String memberName) {
        return new IllegalArgumentException("Member " + memberName + " not found in " + type.getQualifiedName());
    }

    private boolean hasMember(final String memberName) {
        return type.getEnclosedElements().stream()
                .filter(element -> element instanceof VariableElement)
                .map(element -> element.getSimpleName().toString())
                .anyMatch(name -> name.equals(memberName));
    }

    private int codeElementIndex(final String elementName, final String ...patterns) {
        return Stream.of(patterns).map(pattern -> String.format(pattern, elementName))
                .filter(term -> sourceCode.contains(term))
                .map(term -> sourceCode.indexOf(term))
                .findFirst().orElse(-1);
    }

}
