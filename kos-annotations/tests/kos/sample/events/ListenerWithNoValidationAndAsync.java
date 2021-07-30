package kos.sample.events;

import io.vertx.core.Future;
import kos.events.Listener;

public class ListenerWithNoValidationAndAsync {

    @Listener("gcp::pubsub::users::deleted")
    Future<Void> on(String username) {
        throw new RuntimeException("Not yet implemented");
    }
}