package kos.sample.events;

import kos.events.Listener;

public class ListenerWithInvalidReturnType {

    @Listener("my:topic")
    String listener(String something, String anotherMessage) {
        throw new RuntimeException("Not yet implemented");
    }
}