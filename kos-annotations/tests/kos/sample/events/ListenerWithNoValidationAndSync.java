package kos.sample.events;

import kos.events.Listener;

public class ListenerWithNoValidationAndSync {

    @Listener("gcp::pubsub::users::deleted")
    void on(String username) {
        throw new RuntimeException("Not yet implemented");
    }
}