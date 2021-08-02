package kos.sample.events;

import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import kos.api.*;
import kos.core.events.*;

/**
 * Auto generated event bus listener configuration for {@link ListenerWithValidationAndBothAsyncAndSync }.
 */
@SuppressWarnings("all")
@injector.Singleton
@javax.annotation.processing.Generated("kos.apt.EventListenerKosProcessor")
public class ListenerWithValidationAndBothAsyncAndSyncEventListenerConfiguration implements ConfigurationLoadedEventListener {

    @Override
    public void on(ConfigurationLoadedEvent event) {
        final ImplementationLoader implementationLoader = event.getKosContext().getImplementationLoader();

        // Auto-configure a message producer, if found in the classpath
        final EventBusSinkManager messageProducerSyncManager = implementationLoader.instanceOfOrFail(EventBusSinkManager.class);

        final Validation validation = event.getKosContext().getDefaultValidation();
        final Vertx vertx = event.getKosContext().getDefaultVertx();
        final ListenerWithValidationAndBothAsyncAndSync listener = implementationLoader.instanceOfOrFail(ListenerWithValidationAndBothAsyncAndSync.class);

        /*
         * Configuring listener for
         *  - eventBus address: "gcp::pubsub::users::deleted"
         *  - handled by: ListenerWithValidationAndBothAsyncAndSync#onDeleteByUsername
         *  - handler is async: true
         *  - requires validation: true
         */
        java.util.function.Function<java.lang.String, Future<Void>> validEventHandler1 = message -> {
            return listener.onDeleteByUsername(message);
        };
        EventBusSink.SubscriptionRequest subscriptionRequest1 = new EventBusSink.SubscriptionRequest(
            event.getApplicationConfig(), event.getKosContext(), "gcp::pubsub::users::deleted", java.lang.String.class
        );
        messageProducerSyncManager.tryInitialise(subscriptionRequest1);
        vertx.eventBus().consumer("gcp::pubsub::users::deleted", EventHandler.async((Message<java.lang.String> message) -> {
            java.lang.String body = message.body();
            return validation.validate(body)
                .compose(validEventHandler1);
        }));
        /*
         * Configuring listener for
         *  - eventBus address: "gcp::pubsub::users::deleted"
         *  - handled by: ListenerWithValidationAndBothAsyncAndSync#onDeleteById
         *  - handler is async: false
         *  - requires validation: true
         */
        java.util.function.Function<java.util.UUID, Future<Void>> validEventHandler2 = message -> {
            listener.onDeleteById(message);
            return Future.succeededFuture();
        };
        EventBusSink.SubscriptionRequest subscriptionRequest2 = new EventBusSink.SubscriptionRequest(
            event.getApplicationConfig(), event.getKosContext(), "gcp::pubsub::users::deleted", java.util.UUID.class
        );
        messageProducerSyncManager.tryInitialise(subscriptionRequest2);
        vertx.eventBus().consumer("gcp::pubsub::users::deleted", EventHandler.async((Message<java.util.UUID> message) -> {
            java.util.UUID body = message.body();
            return validation.validate(body)
                .compose(validEventHandler2);
        }));
    }
}