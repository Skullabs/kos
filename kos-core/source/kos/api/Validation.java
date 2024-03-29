package kos.api;

import io.vertx.core.Future;

/**
 * A validation rule for the given type.
 */
public interface Validation<T> {

    /**
     * Returns a static representation of the object being validated.
     */
    @SuppressWarnings("rawtypes")
    Class<T> getTypeOfTheObjectBeingValidated();

    /**
     * Validates the {@code object}. It is expected that the validated object
     * will be returned in the successful {@link Future} result.
     *
     * @param object the object to be validated
     * @param targetClass
     */
    Future<T> validate(T object, Class<T> targetClass);
}
