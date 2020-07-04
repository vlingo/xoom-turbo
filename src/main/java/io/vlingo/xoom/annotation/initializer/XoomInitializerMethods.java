// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import io.vlingo.actors.Stage;
import io.vlingo.actors.World;
import io.vlingo.common.identity.IdentityGeneratorType;
import io.vlingo.http.resource.Configuration;
import io.vlingo.http.resource.Resources;
import io.vlingo.http.resource.Server;
import io.vlingo.xoom.actors.Settings;
import io.vlingo.xoom.actors.StageInitializationAware;
import io.vlingo.xoom.annotation.initializer.AddressFactory.IdentityGenerator;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.annotation.initializer.XoomInitializerStatements.*;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class XoomInitializerMethods {

    private final Elements elements;
    private final Xoom xoomAnnotation;
    private final TypeElement bootstrapClass;

    public static List<MethodSpec> from(final ProcessingEnvironment environment,
                                        final Element bootstrapClass) {
        final XoomInitializerMethods methods =
                new XoomInitializerMethods(environment, bootstrapClass);

        return Arrays.asList(methods.mainMethod(),
                methods.resolvePortMethod(),
                methods.constructorMethod());
    }

    private XoomInitializerMethods(final ProcessingEnvironment environment,
                                   final Element bootstrapClass) {
        this.elements = environment.getElementUtils();
        this.bootstrapClass = (TypeElement) bootstrapClass;
        this.xoomAnnotation = bootstrapClass.getAnnotation(Xoom.class);
    }

    private MethodSpec mainMethod() {
        return MethodSpec.methodBuilder("main")
                .addModifiers(PUBLIC, STATIC).returns(void.class)
                .addParameter(String[].class, "args").addException(Exception.class)
                .addStatement(INITIALIZER_INSTANCE_STATEMENT, xoomAnnotation.name())
                .build();
    }

    private MethodSpec resolvePortMethod() {
        return MethodSpec.methodBuilder("resolvePort")
                .addModifiers(PUBLIC, STATIC).returns(TypeName.INT)
                .addParameter(String[].class, "args")
                .beginControlFlow("try")
                .addStatement("return Integer.parseInt(args[0])")
                .nextControlFlow("catch (final $T e)", Exception.class)
                .addStatement("System.out.println(\"$L: Command line does not provide a valid port; defaulting to: \" + DEFAULT_PORT)", xoomAnnotation.name())
                .addStatement("return DEFAULT_PORT")
                .endControlFlow()
                .build();
    }

    private MethodSpec constructorMethod() {
        final Xoom xoomAnnotation =
                bootstrapClass.getAnnotation(Xoom.class);

        final String blockingMailboxStatement =
                xoomAnnotation.blocking() ? BLOCKING_MAILBOX_ENABLING_STATEMENT :
                        BLOCKING_MAILBOX_DISABLING_STATEMENT;

        final String onInitInvocation = resolveInitializerAwareInvocation();
        final Entry<String, Object[]> resourcesStatement = resolveResourcesStatement();
        final Entry<String, Object[]> stageInstanceStatement = resolveStageInstanceStatement();

        return MethodSpec.constructorBuilder()
                .addModifiers(PUBLIC).addParameter(Integer.class, "port")
                .addStatement(blockingMailboxStatement, Settings.class)
                .addStatement(WORLD_INSTANCE_STATEMENT, World.class, xoomAnnotation.name(), Settings.class)
                .addStatement(stageInstanceStatement.getKey(), stageInstanceStatement.getValue())
                .addStatement(onInitInvocation, ClassName.get(bootstrapClass.asType()))
                .addStatement(resourcesStatement.getKey(), resourcesStatement.getValue())
                .addStatement(SERVER_INSTANCE_STATEMENT, Server.class, Configuration.Sizing.class, Configuration.Timing.class)
                .addStatement(SHUTDOWN_HOOK_STATEMENT, xoomAnnotation.name())
                .build();
    }

    private String resolveInitializerAwareInvocation() {
        final TypeMirror stageInitializerAwareInterface =
                elements.getTypeElement(StageInitializationAware.class.getCanonicalName()).asType();

        return bootstrapClass.getInterfaces().contains(stageInitializerAwareInterface) ?
                INSTANTIATION_STATEMENT + STAGE_INITIALIZER_STATEMENT : INSTANTIATION_STATEMENT;
    }

    private Entry<String, Object[]> resolveStageInstanceStatement() {
        if(xoomAnnotation.addressFactory().type().isBasic()) {
            return new SimpleEntry(BASIC_STAGE_INSTANCE_STATEMENT,
                    new Object[]{Stage.class, xoomAnnotation.name()});
        }

        final IdentityGenerator generator =
                xoomAnnotation.addressFactory().generator();

        final IdentityGeneratorType identityGeneratorType =
                generator.resolveWith(xoomAnnotation.addressFactory().type());

        return new SimpleEntry(STAGE_INSTANCE_STATEMENT,
                new Object[]{Stage.class, xoomAnnotation.name(), Stage.class,
                        xoomAnnotation.addressFactory().type().clazz,
                        IdentityGeneratorType.class, identityGeneratorType});
    }

    private Entry<String, Object[]> resolveResourcesStatement() {
        final Object[] defaultParameters = new Object[]{Resources.class, Resources.class};

        final io.vlingo.xoom.annotation.initializer.Resources resourcesAnnotation =
                bootstrapClass.getAnnotation(io.vlingo.xoom.annotation.initializer.Resources.class);

        if(resourcesAnnotation == null) {
            return new SimpleEntry(String.format(RESOURCES_STATEMENT_PATTERN, ""), defaultParameters);
        }

        final Object[] endpointClasses = retrieveResourcesClasses(resourcesAnnotation);

        final String routesStatement =
                Stream.of(endpointClasses).map(clazz -> ROUTES_STATEMENT)
                        .collect(Collectors.joining(", "));

        return new SimpleEntry(String.format(RESOURCES_STATEMENT_PATTERN, routesStatement),
                ArrayUtils.addAll(defaultParameters, endpointClasses));
    }

    private Object[] retrieveResourcesClasses(final io.vlingo.xoom.annotation.initializer.Resources resourcesAnnotation) {
        try {
            return resourcesAnnotation.value();
        } catch (final MirroredTypesException exception) {
            return exception.getTypeMirrors().toArray();
        }
    }

}
