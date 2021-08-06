package kos.sample.validation;

import io.vertx.core.Future;
import kos.validation.Validates;

import java.util.Objects;

public class ValidatorWithMultipleValidations {

    @Validates
    Future<java.util.List<String>> validate(java.util.List<String> listOfString) {
        return Future.succeededFuture(listOfString);
    }

    @Validates
    void validate(java.awt.List list) {
        Objects.requireNonNull(list);
    }
}
