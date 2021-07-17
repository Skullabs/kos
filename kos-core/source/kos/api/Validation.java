package kos.api;

import io.vertx.core.Future;

/**
 * A validation rule for the given type.
 */
public interface Validation {

    /**
     * Returns a static representation of the object being validated.
     */
    @SuppressWarnings("rawtypes")
    Class getTypeOfTheObjectBeingValidated();

    /**
     * Validates the {@code object}. It is expected that the validated object
     * will be returned in the successful {@link Future} result.
     *
     * @param type the type of the object being validated - useful in case of null
     * @param object the object to be validated
     */
    <T> Future<T> validate(Class<T> type, T object);
}
