package kos.sample.events;

import io.vertx.core.Future;
import kos.events.Listener;
import kos.validation.Valid;
import lombok.Value;

import java.util.UUID;

public class ListenerWithValidationAndBothAsyncAndSync {

    @Listener("gcp::pubsub::users::deleted")
    Future<Void> onDeleteByUsername(@Valid String username) {
        throw new RuntimeException("Not yet implemented");
    }

    @Listener("gcp::pubsub::users::deleted")
    void onDeleteById(@Valid UUID id) {
        throw new RuntimeException("Not yet implemented");
    }
}