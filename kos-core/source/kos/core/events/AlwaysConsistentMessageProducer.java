package kos.core.events;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageProducer;
import io.vertx.core.impl.future.SucceededFuture;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A bespoke {@link MessageProducer} implementation designed to provide <i>always consistent</i>
 * semantics when sending messages to Vertx' EventBus. This class was implemented to allow
 * {@link kos.api.EventPublisherSink} to reliably send messages to a remote event broker, hence
 * a few of its methods are just stubs and might fail if used outside this context.
 */
@RequiredArgsConstructor
class AlwaysConsistentMessageProducer<T> implements MessageProducer<T> {

    private final String address;
    private final EventBus eventBus;

    @Override
    public String address() {
        return address;
    }

    @Override
    public Future<Void> write(T body) {
        return eventBus
            .request(address, body)
            .compose(message -> Future.succeededFuture());
    }

    @Override
    public Future<Void> close() {
        return Future.succeededFuture();
    }

    @Override
    public void close(Handler<AsyncResult<Void>> handler) {
        handler.handle(new SucceededFuture<>(null));
    }

    @Override
    public MessageProducer<T> deliveryOptions(DeliveryOptions options) {
        throw new UnsupportedOperationException("The method 'deliveryOptions' is not available in this context.");
    }

    @Override
    public void write(T body, Handler<AsyncResult<Void>> handler) {
        throw new UnsupportedOperationException("The method 'write' is not available in this context.");
    }
}
