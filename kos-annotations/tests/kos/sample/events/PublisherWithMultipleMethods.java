package kos.sample.events;

import io.vertx.core.Future;
import kos.events.Publisher;

import java.util.UUID;

public interface PublisherWithMultipleMethods {

    @Publisher("gcp::pubsub::users::deleted")
    Future<Void> userDeleted(UUID uuid);
}


