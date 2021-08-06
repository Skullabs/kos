package kos.sample.validation;

import io.vertx.core.Future;
import kos.validation.Validates;

import java.util.Objects;
import java.util.UUID;

public class AsyncValidator {

    @Validates
    Future<UUID> validate(UUID uuid) {
        Objects.requireNonNull(uuid);
        return Future.succeededFuture(uuid);
    }
}
