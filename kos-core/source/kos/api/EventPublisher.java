package kos.api;

import io.vertx.core.Future;
import io.vertx.core.eventbus.MessageProducer;

public interface EventPublisher<REQ> {

    default Future<Void> send(REQ requestPayload) {
        return getMessageProducer().write(requestPayload);
    }

    String getAddress();

    MessageProducer<REQ> getMessageProducer();
}
