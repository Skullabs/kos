package kos.core.events;

import io.netty.util.internal.ThrowableUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.eventbus.ReplyFailure;

import java.io.PrintWriter;
import java.io.StringWriter;

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
            message.reply(message.address());
        } else {
            message.fail(1, convertThrowableToMessage(result.cause()));
        }
    }

    private String convertThrowableToMessage(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        return stringWriter.toString();
    }
}