package kos.sample.validation;

import injector.Singleton;
import kos.validation.Validates;

import java.util.Objects;
import java.util.UUID;

@Singleton
public class SyncValidator {

    @Validates
    void validate(UUID uuid) {
        Objects.requireNonNull(uuid);
    }
}
