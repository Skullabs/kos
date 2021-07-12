package kos.core.validation;

import kos.core.exception.KosException;
import lombok.Getter;

public class ValidationException extends KosException {

    @Getter
    private final FailureType failureType;

    public ValidationException(FailureType failureType, String message) {
        super((Throwable) null, message);
        this.failureType = failureType;
    }
}
