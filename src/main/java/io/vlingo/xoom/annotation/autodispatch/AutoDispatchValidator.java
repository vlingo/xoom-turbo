package io.vlingo.xoom.annotation.autodispatch;

import io.vlingo.xoom.annotation.AnnotatedElements;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.Arrays;

import static io.vlingo.xoom.annotation.Validation.*;
import static io.vlingo.xoom.annotation.autodispatch.AutoDispatchValidations.*;

public class AutoDispatchValidator {

    private static AutoDispatchValidator instance;

    private AutoDispatchValidator() {}

    public static AutoDispatchValidator instance() {
        if(instance == null) {
            instance = new AutoDispatchValidator();
        }
        return instance;
    }

    public void validate(final ProcessingEnvironment processingEnvironment, final AnnotatedElements annotatedElements) {
        Arrays.asList(isInterface(), classVisibilityValidation(), isQueryInterface(), queryWithoutModelValidator())
                .forEach(validator ->
                        validator.validate(processingEnvironment, Queries.class, annotatedElements));

        Arrays.asList(isInterface(), classVisibilityValidation(), isModelInterface(), modelWithoutQueryValidator())
                .forEach(validator ->
                        validator.validate(processingEnvironment, Model.class, annotatedElements));

    }

}


