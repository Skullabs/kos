package kos.core.events;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.Flow;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Handles received events asynchronously. It will always acknowledge to the
 * publisher that the process has been finished, being it a successful attempt
 * or not. Publishers will be able to receive the reply only when using Vert.x
 * EventLoop Req/Reply pattern.
 */
public interface EventHandler<T> extends Handler<Message<T>> {

    @Override
    default void handle(Message<T> event) {
        tryHandle(event)
            .onComplete(new DefaultAsyncEventReplier<>(event));
    }

    Future<Void> tryHandle(Message<T> event);

    static  <T> EventHandler<T> sync(Consumer<Message<T>> handler) {
        return new SyncEventHandler<>(handler);
    }

    static  <T> EventHandler<T> async(Function<Message<T>, Future<Void>> handler) {
        return new AsyncEventHandler<>(handler);
    }
}

@RequiredArgsConstructor
class AsyncEventHandler<T> implements EventHandler<T> {

    private final Function<Message<T>, Future<Void>> handler;

    @Override
    public Future<Void> tryHandle(Message<T> event) {
        try {
            return handler.apply(event);
        } catch (Throwable cause) {
            return Future.failedFuture(cause);
        }
    }
}

@RequiredArgsConstructor
class SyncEventHandler<T> implements EventHandler<T> {

    private final Consumer<Message<T>> handler;

    @Override
    public Future<Void> tryHandle(Message<T> event) {
        try {
            handler.accept(event);
            return Future.succeededFuture();
        } catch (Throwable cause) {
            return Future.failedFuture(cause);
        }
    }
}