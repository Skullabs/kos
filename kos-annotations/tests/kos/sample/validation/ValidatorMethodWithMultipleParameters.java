package kos.sample.validation;

import kos.validation.Validates;

import java.util.UUID;

public class ValidatorMethodWithMultipleParameters {

    @Validates
    void validate(String s, UUID u) {
        throw new RuntimeException("Not yet implemented");
    }
}
