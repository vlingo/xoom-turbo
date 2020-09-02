package io.vlingo.xoom.annotation.autodispatch;

import io.vlingo.xoom.annotation.AnnotatedElements;

import java.util.Arrays;

import static io.vlingo.xoom.annotation.Validation.*;

public class AutoDispatchValidator {

    private static AutoDispatchValidator instance;

    private AutoDispatchValidator() {}

    public static AutoDispatchValidator instance() {
        if(instance == null) {
            instance = new AutoDispatchValidator();
        }
        return instance;
    }

    public void validate(final AnnotatedElements annotatedElements) {
        Arrays.asList(isInterface())
                .forEach(validator -> {
//                    validator.validate(Route.class, annotatedElements);
                    validator.validate(Queries.class, annotatedElements);
//                    validator.validate(Model.class, annotatedElements);
                });
    }

}
