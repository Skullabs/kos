package kos.sample.validation;

import io.vertx.core.Future;
import kos.api.Validation;

/**
 * Auto generated validation for type {@link java.util.UUID }.
 */
@SuppressWarnings("all")
@injector.Singleton
@javax.annotation.processing.Generated("kos.apt.validation.ValidatorProcessor")
public class SyncValidator$UUID$Validation1 implements Validation<java.util.UUID> {

    private final SyncValidator validator;

    @injector.Constructor
    public SyncValidator$UUID$Validation1(SyncValidator validator) {
        this.validator = validator;
    }

    @Override
    public Future<java.util.UUID> validate(java.util.UUID object) {
        try {
            validator.validate(object);
            return Future.succeededFuture(object);
        } catch (Throwable cause) {
            return Future.failedFuture(cause);
        }
    }

    @Override
    public Class<java.util.UUID> getTypeOfTheObjectBeingValidated() {
        return java.util.UUID.class;
    }
}
