package kos.core.events;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;

/**
 * Automatically replies to async events received from EventBus. In case
 * of success, it will reply the address. In case of failure it will notify
 * the sender, relaying the cause as a response.
 */
public class DefaultAsyncEventReplier<T> implements Handler<AsyncResult<Void>> {

    private final Message<T> message;

    public DefaultAsyncEventReplier(Message<T> message) {
        this.message = message;
    }

    @Override
    public void handle(AsyncResult<Void> result) {
        if (result.succeeded()) {
            message.replyAddress();
        } else {
            message.fail(1, result.cause().getMessage());
            result.cause().printStackTrace();
        }
    }
}