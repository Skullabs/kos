package kos.sample.validation;

import io.vertx.core.Future;
import kos.api.Validation;

/**
 * Auto generated validation for type {@link java.util.List }.
 */
@SuppressWarnings("all")
@injector.Singleton
@javax.annotation.processing.Generated("kos.apt.validation.ValidatorProcessor")
public class ValidatorWithMultipleValidations$List$Validation1 implements Validation<java.util.List> {

    private final ValidatorWithMultipleValidations validator;

    @injector.Constructor
    public ValidatorWithMultipleValidations$List$Validation1(ValidatorWithMultipleValidations validator) {
        this.validator = validator;
    }

    @Override
    public Future<java.util.List> validate(java.util.List object, Class targetClass) {
        try {
            return validator.validate(object);
        } catch (Throwable cause) {
            return Future.failedFuture(cause);
        }
    }

    @Override
    public Class<java.util.List> getTypeOfTheObjectBeingValidated() {
        return java.util.List.class;
    }
}
