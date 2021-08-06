package kos.sample.validation;

import kos.validation.Validates;

public class ValidatorMethodWithInvalidReturnType {

    @Validates
    Object validate(String s) {
        throw new RuntimeException("Not yet implemented");
    }
}
