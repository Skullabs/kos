package kos.sample.validation;

import io.vertx.core.Future;
import kos.api.Validation;

/**
 * Auto generated validation for type {@link java.awt.List }.
 */
@SuppressWarnings("all")
@injector.Exposed
@javax.annotation.processing.Generated("kos.apt.validation.ValidatorProcessor")
public class ValidatorWithMultipleValidations$List$Validation2 implements Validation<java.awt.List> {

    private final ValidatorWithMultipleValidations validator;

    @injector.Constructor
    public ValidatorWithMultipleValidations$List$Validation2(ValidatorWithMultipleValidations validator) {
        this.validator = validator;
    }

    @Override
    public Future<java.awt.List> validate(java.awt.List object, Class targetClass) {
        try {
            validator.validate(object);
            return Future.succeededFuture(object);
        } catch (Throwable cause) {
            return Future.failedFuture(cause);
        }
    }

    @Override
    public Class<java.awt.List> getTypeOfTheObjectBeingValidated() {
        return java.awt.List.class;
    }
}
