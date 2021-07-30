package kos.sample.events;

import kos.events.Listener;

public class ListenerWithMultipleParameters {

    @Listener("my:topic")
    void listener(String something, String anotherMessage) {
        throw new RuntimeException("Not yet implemented");
    }
}